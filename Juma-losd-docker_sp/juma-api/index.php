<?php
  /*
  Title: API for JUMA Uplift Tool.
  Version: v0.1 
  Developed by: Alan Meehan.
  Date: July 2018.
  */
	
	$path_to_juma_base = "/juma/juma-uplift-master";
	
	$path_to_jumaapi = getcwd();
	$path_to_jumaapi = str_replace( "\\" ,"/" ,$path_to_jumaapi);
	
	$user_id = null;
	$map_id = null;
	$source_data = null;
	
	$rdf_data = null;
	
	// Getting data POST'ed to API
  if( isset($_REQUEST['user']) ){
		$user_id = $_REQUEST['user'];
	}
	else {
		exit("Error: 'user' parameter missing from call");
	}
  if( isset($_REQUEST['map']) ){
		$map_id = $_REQUEST['map'];
	}
	else {
		exit("Error: 'map' parameter missing from call");
	}
  if( isset($_REQUEST['source']) ){
		$source_data = $_REQUEST['source'];
	}
	else {
		exit("Error: 'source' parameter missing from call");
	}
	
	//Checking to see if mapping file exists
	if ( !file_exists($path_to_juma_base."/userfiles/".$user_id."/".$map_id."/".$map_id."_mapping.ttl") ){
		exit("Error: Unable to find mapping file, ensure mappingID and userID is correct!");
	}
	
	//Create temp directory for user source data file
	if ( !file_exists("temp/".$user_id."/".$map_id."") ){
		mkdir("./temp/".$user_id."/".$map_id."", 0777, true);
	}

	$headers = [
                'Accept: text/csv; charset=utf-8',
                'User-Agent: Mozilla/5.0 (X11; Ubuntu; Linux i686; rv:28.0) Gecko/20100101 Firefox/28.0',
        ];
        $options = array(
                CURLOPT_RETURNTRANSFER => true,   // return web page
                CURLOPT_HEADER         => false,  // don't return headers
                CURLOPT_FOLLOWLOCATION => true,   // follow redirects
                CURLOPT_MAXREDIRS      => 2,     // stop after 2 redirects
                CURLOPT_ENCODING       => "",     // handle compressed
                CURLOPT_USERAGENT      => "test", // name of client
                CURLOPT_AUTOREFERER    => true,   // set referrer on redirect
                CURLOPT_CONNECTTIMEOUT => 120,    // time-out on connect
                CURLOPT_TIMEOUT        => 120,    // time-out on response
                CURLOPT_HTTPHEADER     => $headers,
                CURLOPT_SSL_VERIFYHOST => 0,
                CURLOPT_SSL_VERIFYPEER => 0
        );
	$curl = curl_init($source_data);
        curl_setopt_array($curl, $options);
        $csv_from_web  = curl_exec($curl);
        $errors  = curl_error($curl);
        curl_close ($curl);
	
	$csv_file = fopen("temp/".$user_id."/".$map_id."/sourcedata.csv", "w") or exit("Error: Unable to access this URL, try URL encoding it!");
	fwrite($csv_file, $csv_from_web);
	fclose($csv_file);
	
	//Check that the data pulled from the web is not blank
	if ( $csv_from_web == "" ){
		exit("Error: Source data is empty - ensure there is data in the file at the specified URL");
	}

	//Creating R2RML config file
	$conf_file = fopen("temp/".$user_id."/".$map_id."/config.properties","w") or exit("Unable to open file!");
	fwrite($conf_file, "mappingFile = ".$path_to_juma_base."/userfiles/".$user_id."/".$map_id."/".$map_id."_mapping.ttl\nCSVFiles = ".$path_to_jumaapi."/temp/".$user_id."/".$map_id."/sourcedata.csv\noutputFile = ".$path_to_jumaapi."/temp/".$user_id."/".$map_id."/output.ttl" );
	fclose($conf_file);
	
	//Perform execution
	shell_exec("java -Xmx4G -jar ".$path_to_juma_base."/r2rml_updated/target/r2rml.jar ".$path_to_jumaapi."/temp/".$user_id."/".$map_id."/config.properties");

	
	//Getting RDF data from file in order to return it
	$output_file = fopen("temp/".$user_id."/".$map_id."/output.ttl", "r") or exit("Error: Unable to find output file!");
	while(!feof($output_file)) {
		$rdf_temp = fgets($output_file);
		$rdf_temp = str_replace("%20", "_", $rdf_temp);
		$rdf_data .= $rdf_temp."\n";
	}
	fclose($output_file);
	
	
	//Return RDF data
	echo $rdf_data;

	//Cleaning up temp files
	unlink("temp/".$user_id."/".$map_id."/sourcedata.csv");
  	unlink("temp/".$user_id."/".$map_id."/config.properties");
	unlink("temp/".$user_id."/".$map_id."/output.ttl");
	rmdir("temp/".$user_id."/".$map_id."");
	rmdir("temp/".$user_id."");
?>
