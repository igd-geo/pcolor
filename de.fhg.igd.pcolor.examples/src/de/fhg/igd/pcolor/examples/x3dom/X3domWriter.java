package de.fhg.igd.pcolor.examples.x3dom;

import java.io.PrintWriter;

/**
 * Simple helper methods to construct digestible x3dom/html.
 * @author Simon Thum
 */
public class X3domWriter {

	
	public static void putHtmlX3domScene(PrintWriter w) {
		// emit html5 x3dom header
		w.append("<!DOCTYPE html>\r\n<html>\r\n" +
				"<head>\r\n" +
			    "    <meta http-equiv='X-UA-Compatible' content='chrome=1'>\r\n" +
				"    <meta http-equiv='Content-Type' content='text/html;charset=utf-8'>\r\n" +
			    "    <link rel=\"stylesheet\" type=\"text/css\" href=\"http://x3dom.org/download/1.4/x3dom.css\">\r\n" + 
			    // "    <link rel=\"stylesheet\" type=\"text/css\" href=\"http://x3dom.org/download/x3dom-v1.2.css\">\r\n" +
			    "    <script type=\"text/javascript\" src=\"http://x3dom.org/download/1.4/x3dom.js\"></script>" +
			    // "    <script type=\"text/javascript\" src=\"http://x3dom.org/download/x3dom-v1.2.js\"></script>" +
				"</head>\r\n");
	
		// open x3dom scene
		w.append(
				"<body>\r\n" + 
				"<x3d xmlns=\"http://www.web3d.org/specifications/x3d-namespace\" id=\"x3d-container\" showStat=\"true\" x=\"0px\" y=\"0px\" width=\"1200px\" height=\"800px\" style='background-color: #757575'>\r\n" + 
				"  <scene DEF='scene'>\r\n" + 
				"    <navigationInfo headlight='false' type='\"EXAMINE\" \"FLY\"' ></navigationInfo>\r\n");
	}
	
	public static void closeHtmlX3domScene(PrintWriter w) {
		// close scene
		w.append("      </scene>\r\n" + 
				"    </x3d>\r\n" + 
				"  </body>\r\n" + 
				"</html>\r\n");
	}


}
