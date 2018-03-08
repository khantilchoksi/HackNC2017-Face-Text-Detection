import java.io.File;
import java.io.FileReader;
import java.io.PrintWriter;
import java.util.Iterator;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.Font.FontFamily;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.PdfWriter;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
//import sandbox.WrapToTest;


public class Text2PDF {
	
	 public static final String TEXT
     = "C:\\Users\\Dax Amin\\Downloads\\result(1).txt";
	 public static final String DEST
     = "C:\\Users\\Dax Amin\\Downloads\\text2pdf.pdf";
	
	public static void main(String[] args){
		
		JSONParser parser = new JSONParser();
		try{
			Object obj = parser.parse(new FileReader("C:\\Users\\Dax Amin\\Downloads\\result(1).json"));
			JSONObject json = (JSONObject) obj;
			JSONArray text =  (JSONArray)json.get("textAnnotations");
			
			Iterator<JSONObject> iterator = text.iterator();
			
			JSONObject textobj = iterator.next();
			String desc = (String) textobj.get("description");
			System.out.println(desc);
			File file = new File("C:\\Users\\Dax Amin\\Downloads\\result(1).txt");

			PrintWriter pw = new PrintWriter(file);
			pw.println(desc);
			pw.close();
			
			File file1 = new File(DEST);
	        file1.getParentFile().mkdirs();
	    	new Text2PDF().createPdf(DEST);
	} catch (Exception e) {
        e.printStackTrace();
	}
	}
	
	public void createPdf(String dest)
			throws DocumentException, IOException {
		        Document document = new Document();
		        PdfWriter.getInstance(document, new FileOutputStream(dest));
		        document.open();
		        BufferedReader br = new BufferedReader(new FileReader(TEXT));
		        String line;
		        Paragraph p;
		        Font normal = new Font(FontFamily.TIMES_ROMAN, 12);
		        Font bold = new Font(FontFamily.TIMES_ROMAN, 12, Font.BOLD);
		        boolean title = true;
		        while ((line = br.readLine()) != null) {
		            p = new Paragraph(line, title ? bold : normal);
		            p.setAlignment(Element.ALIGN_JUSTIFIED);
		            title = line.isEmpty();
		            document.add(p);
		        }
		        document.close();
		    }

}
