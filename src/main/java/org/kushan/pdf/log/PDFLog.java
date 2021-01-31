package org.kushan.pdf.log;

import java.awt.Color;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.management.ManagementFactory;
import java.lang.management.OperatingSystemMXBean;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.imageio.ImageIO;
import javax.xml.parsers.ParserConfigurationException;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartUtilities;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PiePlot3D;
import org.jfree.data.general.DefaultPieDataset;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.openqa.selenium.Capabilities;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.xml.sax.SAXException;

import com.itextpdf.text.BaseColor;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Font;
import com.itextpdf.text.Image;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;

public class PDFLog {
	public enum Level {
		  Info,
		  Warning,
		  Error
		}
	
	 public static void createReport() throws SAXException, IOException, ParserConfigurationException, DocumentException, ParseException, InterruptedException
	{
		
		  JSONParser jsonParser = new JSONParser();
	    com.itextpdf.text.Document documentPDF = new com.itextpdf.text.Document();
		
	    Date date = new Date() ;
	    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH-mm-ss") ;
	   String fileName = "Log" + dateFormat.format(date) + ".pdf";
		  PdfWriter.getInstance(documentPDF,
	                new FileOutputStream(fileName));

	    
        documentPDF.open();
        Font font1 = new Font(Font.FontFamily.HELVETICA  , 25, Font.BOLD);
        documentPDF.add(new Paragraph("Log Report for Test Suite", font1));
        
        PdfPTable table = new PdfPTable(3); // 3 columns.

        PdfPCell cell1 = new PdfPCell(new Paragraph("Date & Time"));
        cell1.setBackgroundColor(BaseColor.GRAY);
        PdfPCell cell2 = new PdfPCell(new Paragraph("Status"));
        cell2.setBackgroundColor(BaseColor.GRAY);
        PdfPCell cell3 = new PdfPCell(new Paragraph("Message"));
        cell3.setBackgroundColor(BaseColor.GRAY);
        table.addCell(cell1);
        table.addCell(cell2);
        table.addCell(cell3);
        Font font2 = new Font(Font.FontFamily.HELVETICA  , 15, Font.BOLD);
    	
    	  //Calculate % Test Passed
    	
     
      
      FileReader reader = new FileReader("Logevents.json");
    
        //Read JSON file
        Object obj = jsonParser.parse(reader);
        JSONObject jsonObject = (JSONObject) obj;
      
        JSONArray passList = (JSONArray) jsonObject.get("events");
        reader.close();

 
        

        

		  
		  reader = new FileReader("Logevents.json");
 		   obj = jsonParser.parse(reader);
          jsonObject = (JSONObject) obj;
        
          passList = (JSONArray) jsonObject.get("events");
         
	        //  System.out.println(passList);
	        
	          int infoCount = 0, errorCount = 0 , warningCount = 0;
	          
	          for (int i = 0 ; i<passList.size(); i++)
	          {
	        	 //System.out.println( passList.size());
	        	// System.out.println( passList.get(i));
	        	 PdfPCell celllog = new PdfPCell(new Paragraph(passList.get(i).toString()));
	        	 
	        	 if (passList.get(i).toString().equals("Info"))
	        	 {
	        		
	        		celllog.setBackgroundColor(BaseColor.GREEN);
	        		infoCount = infoCount +1 ;
	        	 }
	        	if (passList.get(i).toString().equals("Error"))
	        	 {
	        		
	        		celllog.setBackgroundColor(BaseColor.RED);
	        		errorCount = errorCount +1;
	        	 }
	        	if (passList.get(i).toString().equals("Warning"))
	        	 {
	        		
	        		celllog.setBackgroundColor(BaseColor.YELLOW);
	        		warningCount = warningCount +1;
	        	 }
	        	
	        	
	        
	        	// table.addCell(passList.get(i).toString());
	            table.addCell(celllog);
	        //	 passList.remove(i);
	    
	        	// System.out.println( passList.get(0));
	          }
	          reader.close();
	        // System.out.println(infoCount);
	         drawBox("Info : "+ infoCount, Color.green, "info.png");
	         drawBox("Error : "+ errorCount, Color.red, "error.png");
	         drawBox("Warning : "+ warningCount, Color.YELLOW, "warning.png");
	        // System.out.println(errorCount); 
	        // System.out.println(warningCount); 
	    
	    	     String infoBox = "info.png"; 
             Image infoBoxes =  Image.getInstance(infoBox);
             infoBoxes.setAbsolutePosition(100f, 700f);
             infoBoxes.scaleAbsolute(100, 50);
 	         documentPDF.add(infoBoxes);
 	         String warningBox = "warning.png"; 
             Image warningBoxes =  Image.getInstance(warningBox);
             warningBoxes.setAbsolutePosition(250f, 700f);
             warningBoxes.scaleAbsolute(100, 50);
 	         documentPDF.add(warningBoxes);
 	         
 	         String errorBox = "error.png"; 
             Image errorBoxes =  Image.getInstance(errorBox);
             errorBoxes.setAbsolutePosition(400f, 700f);
             errorBoxes.scaleAbsolute(100, 50);
 	         documentPDF.add(errorBoxes);
 	        
	             
	      	 DefaultPieDataset dataset = new DefaultPieDataset( );             
         dataset.setValue( "Info" , new Double(infoCount ) );             
         dataset.setValue( "Warning" , new Double( errorCount ) );             
         dataset.setValue( "Error" , new Double( warningCount ) );    
     
         JFreeChart chart = ChartFactory.createPieChart3D( 
            "Log Status Status" ,  // chart title                   
            dataset ,         // data 
            true ,            // include legend                   
            true, 
            false);
         
       
         PiePlot3D plot = ( PiePlot3D ) chart.getPlot( );             
         plot.setStartAngle( 270 );             
         plot.setForegroundAlpha( 0.60f );             
         plot.setInteriorGap( 0.02 );             
         int width1 = 640;   /* Width of the image */             
         int height1 = 480;  /* Height of the image */                             
         File pieChart3D = new File( "pie_Chart3D.jpeg" );                           
         ChartUtilities.saveChartAsJPEG( pieChart3D , chart , width1 , height1 );
         String pieChart = "pie_Chart3D.jpeg"; 
         Image pieChartImage =  Image.getInstance(pieChart);
         pieChartImage.setAbsolutePosition(200f, 500f);
         pieChartImage.scaleAbsolute(200, 200);
	         documentPDF.add(pieChartImage);
	        table.setSpacingBefore(300f);
	        
	 	     reader = new FileReader("Env.json");
	     obj = jsonParser.parse(reader);
         jsonObject = (JSONObject) obj;
   
         passList = (JSONArray) jsonObject.get("env");
    
         // System.out.println(passList);
          
       
          
             
 	        PdfPTable tableenv = new PdfPTable(3); // 3 columns.
 	        PdfPCell headingbrowsername = new PdfPCell(new Paragraph("Browser"));
 	        headingbrowsername.setBackgroundColor(BaseColor.GRAY);
            PdfPCell headingbrowserversion = new PdfPCell(new Paragraph("Browser Version"));
            headingbrowserversion.setBackgroundColor(BaseColor.GRAY);
            PdfPCell headingos = new PdfPCell(new Paragraph("Operating System"));
            headingos.setBackgroundColor(BaseColor.GRAY);
            String str = passList.get(0).toString();
            String cap = str.substring(0, 1).toUpperCase() + str.substring(1);
             PdfPCell cellbrowser =  new PdfPCell(new Paragraph(cap));
             PdfPCell cellbrowserversion = new PdfPCell(new Paragraph(passList.get(1).toString()));
             PdfPCell cellos = new PdfPCell(new Paragraph(passList.get(2).toString()));
             tableenv.addCell(headingbrowsername);
             tableenv.addCell(headingbrowserversion);
             tableenv.addCell(headingos);
             tableenv.addCell(cellbrowser);
             tableenv.addCell(cellbrowserversion);
             tableenv.addCell(cellos);
             tableenv.setSpacingBefore(300f);
             documentPDF.add(tableenv);
             table.setSpacingBefore(10f);
             documentPDF.add(table);
		     documentPDF.close();
		     reader.close();
		     File filede2 = new File("pie_Chart3D.jpeg");
		     filede2.delete();
		     File filedel = new File("info.png");
		     filedel.delete();
		     filedel = new File("error.png");
		     filedel.delete();
		     filedel = new File("warning.png");
		     filedel.delete();
		     filedel = new File("Logevents.json");
		     filedel.delete();
		     filedel = new File("Env.json");
		     filedel.delete();
		   //  filedel = new File("Logevents.json");
		    // filedel.delete();
		     new File("Logs").mkdir();
		    
		     
		     InputStream is = null;
		     OutputStream os = null;
		     try {
		         is = new FileInputStream(fileName);
		         os = new FileOutputStream("Logs/"+fileName);
		         byte[] buffer = new byte[1024];
		         int length;
		         while ((length = is.read(buffer)) > 0) {
		             os.write(buffer, 0, length);
		         }
		     } finally {
		         is.close();
		         os.close();
		     }
		     filedel = new File(fileName);
		     filedel.delete();
		      
		
	}
	
	public static void getEnvVariables (WebDriver driver) throws IOException, ParseException
	{
		  
		   Capabilities cap = ((RemoteWebDriver) driver).getCapabilities();
	        String browserName = cap.getBrowserName().toLowerCase();
	      //  System.out.println(browserName);
	    
	      //  System.out.println(System.getProperty("os.name"));
	        String browserVersion = cap.getVersion().toString();
	      //  System.out.println(browserVersion);
	    	 File fileJSON = new File("Env.json");
	         fileJSON.createNewFile();
        	 FileWriter fw=new FileWriter("Env.json");    
             fw.write("{\"env\":[]}");    
             fw.close(); 
	          
	          //Read JSON file
	             FileReader reader = new FileReader("Env.json");
	             JSONParser jsonParser = new JSONParser();
	             Object obj = jsonParser.parse(reader);
	             JSONObject jsonObject = (JSONObject) obj;
	           
	             JSONArray passList = (JSONArray) jsonObject.get("env");
	             reader.close();
	             //System.out.println(passList);
	             
	             passList.add(browserName);
	             passList.add(browserVersion);
	             passList.add(System.getProperty("os.name"));
	             FileWriter file = new FileWriter("Env.json");
	             file.write(jsonObject.toString());
	    		  file.close();
	    		  
	    		  OperatingSystemMXBean operatingSystemMXBean = ManagementFactory.getOperatingSystemMXBean();
	    		  for (Method method : operatingSystemMXBean.getClass().getDeclaredMethods()) {
	    		    method.setAccessible(true);
	    		    if (method.getName().startsWith("get")
	    		        && Modifier.isPublic(method.getModifiers())) {
	    		            Object value;
	    		        try {
	    		            value = method.invoke(operatingSystemMXBean);
	    		        } catch (Exception e) {
	    		            value = e;
	    		        } // try
	    		        System.out.println(method.getName() + " = " + value);
	    		    } // if
	    		  } // for
	    		  
	    		  
	    	
          
	}
		
	public static void log_event (WebDriver driver, Level log_status, String message ) throws IOException, ParseException
	{
		 String log = null;
		switch(log_status) {
	      case Info:
	        log = "Info";
	        break;
	      case Warning:
	    	  log = "Warning";
	        break;
	      case Error:
	    	  log = "Error";
	        break;
	    }
	    getEnvVariables(driver);
	  	File fileJSON = new File("Logevents.json");
    	if (fileJSON.exists()) 
    	{
    		
    	}
    	else
    	{
    	// fileJSON.createNewFile();
    	 FileWriter fw=new FileWriter("Logevents.json");    
         fw.write("{\"events\":[]}");    
         fw.close(); 
    	}  
  	  JSONParser jsonParser = new JSONParser();
      
      FileReader reader = new FileReader("Logevents.json");
    
        //Read JSON file
        Object obj = jsonParser.parse(reader);
        JSONObject jsonObject = (JSONObject) obj;
      
        JSONArray passList = (JSONArray) jsonObject.get("events");
        reader.close();
        //System.out.println(passList);
        Date log_date = new Date();
        
        passList.add(log_date.toString());
        passList.add(log);
        passList.add(message);
     
         FileWriter file = new FileWriter("Logevents.json");
		  file.write(jsonObject.toString());
		  file.flush();
		  file.close();
	}
	
	public static void drawBox(String text, Color color, String fileName) throws IOException
	{
		 int width = 200, height = 100;


	      BufferedImage bi = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
	      Graphics2D ig2 = bi.createGraphics();
	      ig2.setColor(color);
	      ig2.fillRect(0, 0, bi.getWidth(), bi.getHeight());

	 
	     
	       java.awt.Font font = new  java.awt.Font("Arial", Font.BOLD, 20);
	      
	      ig2.setFont(font);
	      String message = text;
	      FontMetrics fontMetrics = ig2.getFontMetrics();
	      int stringWidth = fontMetrics.stringWidth(message);
	      int stringHeight = fontMetrics.getAscent();
	      //ig2.setBackground(Color.RED);
	      ig2.setPaint(Color.BLACK);
	      ig2.drawString(message, (width - stringWidth) / 2, height / 2 + stringHeight / 4);
         ig2 = bi.createGraphics();
	      ImageIO.write(bi, "PNG", new File(fileName));
	}

}
