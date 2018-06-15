package net.youtoolife.tools;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Iterator;


import org.apache.poi.hssf.usermodel.HSSFFont;
import org.apache.poi.hssf.usermodel.HSSFPalette;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.hssf.util.HSSFColor;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFColor;
import org.apache.poi.xssf.usermodel.XSSFFont;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;



public class XlsParcer {

	
	private String preHEXParce(String val) {
		if (val.length() == 2)
			return val;
		else
			return "0"+val;
	}
	
	private String getHEXColor(short[] triplet) {
		
		String result = "#";
		
		result += preHEXParce(Integer.toHexString(triplet[0]));
		result += preHEXParce(Integer.toHexString(triplet[1]));
		result += preHEXParce(Integer.toHexString(triplet[2]));
		
		//result += "\n/*"+triplet[0] +":"+triplet[1]+":"+triplet[2] +"*\\\n";
		return result;
	}
	
	private String getHEXColor(byte[] triplet) {
		
		String result = "#";
		
		result += preHEXParce(Integer.toHexString(triplet[0]));
		result += preHEXParce(Integer.toHexString(triplet[1]));
		result += preHEXParce(Integer.toHexString(triplet[2]));
		
		//result += "\n/*"+triplet[0] +":"+triplet[1]+":"+triplet[2] +"*\\\n";
		return result;
	}
	
	private String getFontPattern(Cell cell,HSSFWorkbook workBook){       
		
		CellStyle cellStyle = cell.getCellStyle(); 
		
	    HSSFPalette palette = workBook.getCustomPalette();
	    HSSFColor colorTD = palette.getColor(cellStyle.getFillForegroundColor());
	    HSSFFont font = workBook.getFontAt(cellStyle.getFontIndex());
	    HSSFColor colorText = palette.getColor(font.getColor());
	    
	    String result = "";
	    result += "background-color: " + getHEXColor(colorTD.getTriplet()) +"; ";
	    result += "color: " + getHEXColor(colorText.getTriplet()) +"; ";
	    if (font.getItalic())
	    result += "font-style: italic; ";
	    if (font.getBold())
		    result += "font-weight: bold; ";
	    result += "font-size: "+ font.getFontHeightInPoints() +"; ";
	    result += "font-family: " + font.getFontName() +"; ";
	    if (font.getUnderline() > 0)
	    	 result += "text-decoration: underline" + (font.getStrikeout()?", line-through; ":"; ");
	    else if (font.getStrikeout())
	    result += "text-decoration: line-through; ";
	    
	    return result;
	}
	
private String getFontPatternX(XSSFCell cell,XSSFWorkbook workBook){       
		
	    XSSFCellStyle cellStyle = cell.getCellStyle();//(XSSFCellStyle) cell.getCellStyle(); 
		
	    XSSFColor colorTD =  cellStyle.getFillForegroundXSSFColor();// palette.getColor(cellStyle.getFillForegroundColor());
	    XSSFFont font = cellStyle.getFont();//workBook.getFontAt(cellStyle.getFontIndex());
	    //XSSFColor colorText = font.getXSSFColor();//palette.getColor(font.getColor());
	    
	    String result = "";
	    if (colorTD != null && colorTD.getARGBHex() != null)
	    result += "background-color: #" +colorTD.getARGBHex().substring(2)+"; ";//+ getHEXColor(colorTD.getRGB()) +"; ";
	    result += "color: #" + font.getXSSFColor().getARGBHex().substring(2)+"; ";//getHEXColor(colorText.getRGB()) +"; ";
	    if (font.getItalic())
	    result += "font-style: italic; ";
	    if (font.getBold())
		    result += "font-weight: bold; ";
	    result += "font-size: "+ font.getFontHeightInPoints() +"; ";
	    result += "font-family: " + font.getFontName() +"; ";
	    if (font.getUnderline() > 0)
	    	 result += "text-decoration: underline" + (font.getStrikeout()?", line-through; ":"; ");
	    else if (font.getStrikeout())
	    result += "text-decoration: line-through; ";
	    
	    return result;
	}
	
	
	
	public String parceXlsToHtm(String fileName, String sheetName) {
		String result = "<table>\n";
        
        
        InputStream inputStream = null;
        HSSFWorkbook workBook = null;
        //XSSFWorkbook workBook = null;
        try {
            inputStream = new FileInputStream(fileName);
            workBook = new HSSFWorkbook(inputStream);
            //workBook = new XSSFWorkbook(inputStream);
        } catch (IOException e) {
        	Alert alert = new Alert(AlertType.ERROR);
        	alert.setTitle("Error Dialog");
        	alert.setHeaderText("Ooops, there was an error!");
        	alert.setContentText(e.getMessage());

            e.printStackTrace();
            alert.showAndWait();
        }

        Sheet sheet = workBook.getSheet(sheetName);
        //XSSFSheet sheet = workBook.getSheetAt(1);
        
        
        Iterator<Row> it = sheet.iterator();
        
       
        
        while (it.hasNext()) {
            Row row = it.next();
            Iterator<Cell> cells = row.iterator();
            
            
            result += "<tr>\n"; 
            
            outer:
            while (cells.hasNext()) {
            	
                Cell cell = cells.next();
                
                
                
                for (int i = 0; i < sheet.getNumMergedRegions(); i++) {
                    CellRangeAddress region = sheet.getMergedRegion(i); //Region of merged cells

                    int colIndex = region.getFirstColumn(); //number of columns merged
                    int rowNum = region.getFirstRow();      //number of rows merged
                    
                    int colspace = region.getLastColumn() - colIndex;
                    int rowspace = region.getLastRow() - rowNum;
                    //check first cell of the region
                    
                    
                    
                    if (rowNum == cell.getRowIndex() && colIndex == cell.getColumnIndex()) {
                    	Cell mcell = sheet.getRow(rowNum).getCell(colIndex);
                    	
                    	  
                    	///////
                        
                    	
                    	int cellType = mcell.getCellType();
                    	switch (cellType) {
	                    case Cell.CELL_TYPE_STRING:
	                        result += 
	                        		"<td style='text-align: center;' rowspan='"
	                        +(rowspace+1)+"' colspan='"+(colspace+1)+"; "
	                        				+getFontPattern(mcell, workBook) 
	                        				+"'>"+mcell.getStringCellValue()+"</td>\n"; 
	                        
	                        break;
	                    case Cell.CELL_TYPE_NUMERIC:
	                        //result += "[*" + mcell.getNumericCellValue() +"|"+rowspace+":"+colspace + "]";
	                    	result += 
                    		"<td style='text-align: center;' rowspan='"
                    +(rowspace+1)+"' colspan='"+(colspace+1)+"; "
                    				+getFontPattern(mcell, workBook) 
                    				+"'>"+mcell.getNumericCellValue()+"</td>\n"; 
	                        break;
	 
	                    case Cell.CELL_TYPE_FORMULA:
	                    	//result += "<td style='text-align: center; "+
	                        //        getFontPatternX(cell, workBook) +"'>" + cell.getCellFormula()+ "</td>\n";
	                    	switch(cell.getCachedFormulaResultType()) {
	                        case Cell.CELL_TYPE_NUMERIC:
	                        	result += 
	                    		"<td style='text-align: center;' rowspan='"
	                    +(rowspace+1)+"' colspan='"+(colspace+1)+"; "
	                    				+getFontPattern(mcell, workBook) 
	                    				+"'>"+mcell.getNumericCellValue()+"</td>\n"; 
	                            break;
	                        case Cell.CELL_TYPE_STRING:
	                        	 result += 
	                        		"<td style='text-align: center;' rowspan='"
	                        +(rowspace+1)+"' colspan='"+(colspace+1)+"; "
	                        				+getFontPattern(mcell, workBook) 
	                        				+"'>"+mcell.getStringCellValue()+"</td>\n"; 
	                            break;
	                    }
	                        break;
	                    case Cell.CELL_TYPE_BLANK:
	                    //	result += "<td style='text-align: center; "+
	                        //getFontPattern(cell, workBook) +"'> </td>\n";
	                    default:
	                        //result += "[*"+"|"+rowspace+":"+colspace + "]";
	                        break;
	                }
                        
                        continue outer;
                    }
                }

                
                int cellType = cell.getCellType();
                CellStyle cellStyle = cell.getCellStyle();       
      //перебираем возможные типы ячеек
                switch (cellType) {
                    case Cell.CELL_TYPE_STRING:
                        result += "<td style='text-align: center; "+
                    getFontPattern(cell, workBook) +"'>" + cell.getStringCellValue() + "</td>\n";
                        break;
                    case Cell.CELL_TYPE_NUMERIC:
                    	result += "<td style='text-align: center; "+
                                getFontPattern(cell, workBook) +"'>" + cell.getNumericCellValue() + "</td>\n";
                        break;
 
                    case Cell.CELL_TYPE_FORMULA:
                    	//result += "<td style='text-align: center; "+
                        //        getFontPatternX(cell, workBook) +"'>" + cell.getCellFormula()+ "</td>\n";
                    	switch(cell.getCachedFormulaResultType()) {
                        case Cell.CELL_TYPE_NUMERIC:
                        	result += "<td style='text-align: center; "+
                                    getFontPattern(cell, workBook) +"'>" + cell.getNumericCellValue() + "</td>\n";
                            break;
                        case Cell.CELL_TYPE_STRING:
                        	result += "<td style='text-align: center; "+
                                    getFontPattern(cell, workBook) +"'>" + cell.getRichStringCellValue() + "</td>\n";
                            break;
                    }
                        break;
                    case Cell.CELL_TYPE_BLANK:
                    	//result += "<td style='text-align: center; "+
                         //       getFontPattern(cell, workBook) +"'> </td>\n";
                    default:
                        //result += "[ ]";
                        break;
                }
            }
            result += "\n</tr>\n";
        }
        result += "</tabel>";
        System.out.println(result);
        return result;
	}
	
	
	
	public String parceXlsxToHtm(String fileName, String sheetName) {
		String result = "<table>\n";
        
        
        InputStream inputStream = null;
        //HSSFWorkbook workBook = null;
        XSSFWorkbook workBook = null;
        try {
            inputStream = new FileInputStream(fileName);
            //workBook = new HSSFWorkbook(inputStream);
            workBook = new XSSFWorkbook(inputStream);
        } catch (IOException e) {
        	Alert alert = new Alert(AlertType.ERROR);
        	alert.setTitle("Error Dialog");
        	alert.setHeaderText("Ooops, there was an error!");
        	alert.setContentText(e.getMessage());

            e.printStackTrace();
            alert.showAndWait();
        }

        //Sheet sheet = workBook.getSheetAt(1);
        XSSFSheet sheet = workBook.getSheet(sheetName);
        
        
        Iterator<Row> it = sheet.iterator();
        
       
        
        while (it.hasNext()) {
            Row row = it.next();
            Iterator<Cell> cells = row.iterator();
            
            
            result += "<tr>\n"; 
            
            outer:
            while (cells.hasNext()) {
            	
                XSSFCell cell = (XSSFCell) cells.next();
                
                
                
                for (int i = 0; i < sheet.getNumMergedRegions(); i++) {
                    CellRangeAddress region = sheet.getMergedRegion(i); //Region of merged cells

                    int colIndex = region.getFirstColumn(); //number of columns merged
                    int rowNum = region.getFirstRow();      //number of rows merged
                    
                    int colspace = region.getLastColumn() - colIndex;
                    int rowspace = region.getLastRow() - rowNum;
                    //check first cell of the region
                    
                    
                    
                    if (rowNum == cell.getRowIndex() && colIndex == cell.getColumnIndex()) {
                    	XSSFCell mcell = sheet.getRow(rowNum).getCell(colIndex);
                    	
                    	  
                    	///////
                        
                    	
                    	int cellType = mcell.getCellType();
                    	switch (cellType) {
	                    case Cell.CELL_TYPE_STRING:
	                        result += 
	                        		"<td style='text-align: center;' rowspan='"
	                        +(rowspace+1)+"' colspan='"+(colspace+1)+"; "
	                        				+getFontPatternX(mcell, workBook) 
	                        				+"'>"+mcell.getStringCellValue()+"</td>\n"; 
	                        
	                        break;
	                    case Cell.CELL_TYPE_NUMERIC:
	                        //result += "[*" + mcell.getNumericCellValue() +"|"+rowspace+":"+colspace + "]";
	                    	result += 
                    		"<td style='text-align: center;' rowspan='"
                    +(rowspace+1)+"' colspan='"+(colspace+1)+"; "
                    				+getFontPatternX(mcell, workBook) 
                    				+"'>"+mcell.getNumericCellValue()+"</td>\n"; 
	                        break;
	 
	                    case Cell.CELL_TYPE_FORMULA:
	                        //result += "[*" + mcell.getNumericCellValue() +"|"+rowspace+":"+colspace + "]";
	                    
	                    	switch(cell.getCachedFormulaResultType()) {
	                    	case Cell.CELL_TYPE_NUMERIC:
	                    		result += 
	                    		"<td style='text-align: center;' rowspan='"
	                    +(rowspace+1)+"' colspan='"+(colspace+1)+"; "
	                    				+getFontPatternX(mcell, workBook) 
	                    				+"'>"+mcell.getNumericCellValue()+"</td>\n"; 
	                            break;
	                        case Cell.CELL_TYPE_STRING:
	                        	result += 
	                    		"<td style='text-align: center;' rowspan='"
	                    +(rowspace+1)+"' colspan='"+(colspace+1)+"; "
	                    				+getFontPatternX(mcell, workBook) 
	                    				+"'>"+mcell.getRichStringCellValue()+"</td>\n"; 
	                            break;
	                    }
	                        break;
	                    case Cell.CELL_TYPE_BLANK:
	                    //	result += "<td style='text-align: center; "+
	                    //            getFontPatternX(cell, workBook) +"'> </td>\n";
	                    default:
	                        //result += "[*"+"|"+rowspace+":"+colspace + "]";
	                        break;
	                }
                        
                        continue outer;
                    }
                }

                
                int cellType = cell.getCellType();
                CellStyle cellStyle = cell.getCellStyle();       
      //перебираем возможные типы ячеек
                switch (cellType) {
                    case Cell.CELL_TYPE_STRING:
                        result += "<td style='text-align: center; "+
                    getFontPatternX(cell, workBook) +"'>" + cell.getStringCellValue() + "</td>\n";
                        break;
                    case Cell.CELL_TYPE_NUMERIC:
                    	result += "<td style='text-align: center; "+
                                getFontPatternX(cell, workBook) +"'>" + cell.getNumericCellValue() + "</td>\n";
                        break;
 
                    case Cell.CELL_TYPE_FORMULA:
                    	//result += "<td style='text-align: center; "+
                        //        getFontPatternX(cell, workBook) +"'>" + cell.getCellFormula()+ "</td>\n";
                    	switch(cell.getCachedFormulaResultType()) {
                        case Cell.CELL_TYPE_NUMERIC:
                        	result += "<td style='text-align: center; "+
                                    getFontPatternX(cell, workBook) +"'>" + cell.getNumericCellValue() + "</td>\n";
                            break;
                        case Cell.CELL_TYPE_STRING:
                        	result += "<td style='text-align: center; "+
                                    getFontPatternX(cell, workBook) +"'>" + cell.getRichStringCellValue() + "</td>\n";
                            break;
                    }
                        break;
                    case Cell.CELL_TYPE_BLANK:
                    //	result += "<td style='text-align: center; "+
                    //            getFontPatternX(cell, workBook) +"'> </td>\n";
                    default:
                        //result += "[ ]";
                        break;
                }
            }
            result += "\n</tr>\n";
        }
        result += "</tabel>";
        System.out.println(result);
        return result;
	}
	
	public ArrayList<String> parceXls(String fileName, String sheetName) {
		
		ArrayList<String> arr = new ArrayList<>();
		
        InputStream inputStream = null;
        HSSFWorkbook workBook = null;
        //XSSFWorkbook workBook = null;
        try {
            inputStream = new FileInputStream(fileName);
            workBook = new HSSFWorkbook(inputStream);
            //workBook = new XSSFWorkbook(inputStream);
        } catch (IOException e) {
        	Alert alert = new Alert(AlertType.ERROR);
        	alert.setTitle("Error Dialog");
        	alert.setHeaderText("Ooops, there was an error!");
        	alert.setContentText(e.getMessage());

            e.printStackTrace();
            alert.showAndWait();
        }

        Sheet sheet = workBook.getSheet(sheetName);
        //XSSFSheet sheet = workBook.getSheetAt(1);
        
        
        Iterator<Row> it = sheet.iterator();
        
       
        String result = "";
        
        while (it.hasNext()) {
            Row row = it.next();
            Iterator<Cell> cells = row.iterator();
            
            
            outer:
            while (cells.hasNext()) {
            	
                Cell cell = cells.next();
                
                
                
                for (int i = 0; i < sheet.getNumMergedRegions(); i++) {
                    CellRangeAddress region = sheet.getMergedRegion(i); //Region of merged cells

                    int colIndex = region.getFirstColumn(); //number of columns merged
                    int rowNum = region.getFirstRow();      //number of rows merged
                    
                    int colspace = region.getLastColumn() - colIndex;
                    int rowspace = region.getLastRow() - rowNum;
                    //check first cell of the region
 
                    if (rowNum == cell.getRowIndex() && colIndex == cell.getColumnIndex()) {
                    	Cell mcell = sheet.getRow(rowNum).getCell(colIndex);
                    	
                    	
                    	int cellType = mcell.getCellType();
                    	switch (cellType) {
	                    case Cell.CELL_TYPE_STRING:
	                        result += mcell.getStringCellValue(); 
	                        
	                        break;
	                    case Cell.CELL_TYPE_NUMERIC:
	                        //result += "[*" + mcell.getNumericCellValue() +"|"+rowspace+":"+colspace + "]";
	                    	result += String.format("%.0f",mcell.getNumericCellValue()); 
	                        break;
	 
	                    case Cell.CELL_TYPE_FORMULA:
	                    	//result += "<td style='text-align: center; "+
	                        //        getFontPatternX(cell, workBook) +"'>" + cell.getCellFormula()+ "</td>\n";
	                    	switch(cell.getCachedFormulaResultType()) {
	                        case Cell.CELL_TYPE_NUMERIC:
	                        	result += String.format("%.0f",mcell.getNumericCellValue()); 
	                            break;
	                        case Cell.CELL_TYPE_STRING:
	                        	 result += mcell.getStringCellValue(); 
	                            break;
	                    }
	                        break;
	                    case Cell.CELL_TYPE_BLANK:
	                    //	result += "<td style='text-align: center; "+
	                        //getFontPattern(cell, workBook) +"'> </td>\n";
	                    default:
	                        //result += "[*"+"|"+rowspace+":"+colspace + "]";
	                        break;
	                }
                        
                    	System.out.println(result);
                    	arr.add(result);
                    	result = "";
                        continue outer;
                    }
                }

                
                int cellType = cell.getCellType();
                CellStyle cellStyle = cell.getCellStyle();       
      //перебираем возможные типы ячеек
                switch (cellType) {
                    case Cell.CELL_TYPE_STRING:
                        result += cell.getStringCellValue();
                        break;
                    case Cell.CELL_TYPE_NUMERIC:
                    	result += String.format("%.0f",cell.getNumericCellValue());
                        break;
 
                    case Cell.CELL_TYPE_FORMULA:
                    	//result += "<td style='text-align: center; "+
                        //        getFontPatternX(cell, workBook) +"'>" + cell.getCellFormula()+ "</td>\n";
                    	switch(cell.getCachedFormulaResultType()) {
                        case Cell.CELL_TYPE_NUMERIC:
                        	result += String.format("%.0f",cell.getNumericCellValue());
                            break;
                        case Cell.CELL_TYPE_STRING:
                        	result += cell.getRichStringCellValue();
                            break;
                    }
                        break;
                    case Cell.CELL_TYPE_BLANK:
                    	//result += "<td style='text-align: center; "+
                         //       getFontPattern(cell, workBook) +"'> </td>\n";
                    default:
                        //result += "[ ]";
                        break;
                }
                System.out.println(result);
                arr.add(result); 
                result = "";
            }

        }
        
        return arr;
	}
	
	
	
	public ArrayList<String> parceXlsx(String fileName, String sheetName) {
		String result = "";
		ArrayList<String> arr = new ArrayList<>();
        
        InputStream inputStream = null;
        //HSSFWorkbook workBook = null;
        XSSFWorkbook workBook = null;
        try {
            inputStream = new FileInputStream(fileName);
            //workBook = new HSSFWorkbook(inputStream);
            workBook = new XSSFWorkbook(inputStream);
        } catch (IOException e) {
        	Alert alert = new Alert(AlertType.ERROR);
        	alert.setTitle("Error Dialog");
        	alert.setHeaderText("Ooops, there was an error!");
        	alert.setContentText(e.getMessage());

            e.printStackTrace();
            alert.showAndWait();
        }

        //Sheet sheet = workBook.getSheetAt(1);
        XSSFSheet sheet = workBook.getSheet(sheetName);
        
        
        Iterator<Row> it = sheet.iterator();
        
       
        
        while (it.hasNext()) {
            Row row = it.next();
            Iterator<Cell> cells = row.iterator();
            
           
            
            outer:
            while (cells.hasNext()) {
            	
                XSSFCell cell = (XSSFCell) cells.next();
                
                
                
                for (int i = 0; i < sheet.getNumMergedRegions(); i++) {
                    CellRangeAddress region = sheet.getMergedRegion(i); //Region of merged cells

                    int colIndex = region.getFirstColumn(); //number of columns merged
                    int rowNum = region.getFirstRow();      //number of rows merged
                    
                    int colspace = region.getLastColumn() - colIndex;
                    int rowspace = region.getLastRow() - rowNum;
                    //check first cell of the region
                    
                    
                    
                    if (rowNum == cell.getRowIndex() && colIndex == cell.getColumnIndex()) {
                    	XSSFCell mcell = sheet.getRow(rowNum).getCell(colIndex);
                    	
                    	  
                    	///////
                        
                    	
                    	int cellType = mcell.getCellType();
                    	switch (cellType) {
	                    case Cell.CELL_TYPE_STRING:
	                        result += mcell.getStringCellValue(); 
	                        
	                        break;
	                    case Cell.CELL_TYPE_NUMERIC:
	                        //result += "[*" + mcell.getNumericCellValue() +"|"+rowspace+":"+colspace + "]";
	                    	result += String.format("%.0f", mcell.getNumericCellValue());
	                        break;
	 
	                    case Cell.CELL_TYPE_FORMULA:
	                        //result += "[*" + mcell.getNumericCellValue() +"|"+rowspace+":"+colspace + "]";
	                    
	                    	switch(cell.getCachedFormulaResultType()) {
	                    	case Cell.CELL_TYPE_NUMERIC:
	                    		result += String.format("%.0f", mcell.getNumericCellValue());
	                            break;
	                        case Cell.CELL_TYPE_STRING:
	                        	result += mcell.getRichStringCellValue(); 
	                            break;
	                    }
	                        break;
	                    case Cell.CELL_TYPE_BLANK:
	                    //	result += "<td style='text-align: center; "+
	                    //            getFontPatternX(cell, workBook) +"'> </td>\n";
	                    default:
	                        //result += "[*"+"|"+rowspace+":"+colspace + "]";
	                        break;
	                }
                    	System.out.println(result);
                        arr.add(result);
                        result = "";
                        continue outer;
                    }
                }

                
                int cellType = cell.getCellType();
                CellStyle cellStyle = cell.getCellStyle();       
      //перебираем возможные типы ячеек
                switch (cellType) {
                    case Cell.CELL_TYPE_STRING:
                        result += cell.getStringCellValue();
                        break;
                    case Cell.CELL_TYPE_NUMERIC:
                    	result += String.format("%.0f",cell.getNumericCellValue());
                        break;
 
                    case Cell.CELL_TYPE_FORMULA:
                    	//result += "<td style='text-align: center; "+
                        //        getFontPatternX(cell, workBook) +"'>" + cell.getCellFormula()+ "</td>\n";
                    	switch(cell.getCachedFormulaResultType()) {
                        case Cell.CELL_TYPE_NUMERIC:
                        	result += String.format("%.0f",cell.getNumericCellValue());
                            break;
                        case Cell.CELL_TYPE_STRING:
                        	result += cell.getRichStringCellValue();
                            break;
                    }
                        break;
                    case Cell.CELL_TYPE_BLANK:
                    //	result += "<td style='text-align: center; "+
                    //            getFontPatternX(cell, workBook) +"'> </td>\n";
                    default:
                        //result += "[ ]";
                        break;
                }
                System.out.println(result);
                arr.add(result);
                result = "";
            }
        }
        return arr;
	}
	
	
	public ArrayList<String> openFile(String fileName) {
		ArrayList<String> arr = new ArrayList<>();
		if (fileName.charAt(fileName.length()-1) == 'x') {
			InputStream inputStream = null;
	        //HSSFWorkbook workBook = null;
	        XSSFWorkbook workBook = null;
	        try {
	            inputStream = new FileInputStream(fileName);
	            //workBook = new HSSFWorkbook(inputStream);
	            workBook = new XSSFWorkbook(inputStream);
	            for (int i = 0; i < workBook.getNumberOfSheets(); i++)
	            	arr.add(workBook.getSheetAt(i).getSheetName());
	        } catch (IOException e) {
	        	Alert alert = new Alert(AlertType.ERROR);
	        	alert.setTitle("Error Dialog");
	        	alert.setHeaderText("Ooops, there was an error!");
	        	alert.setContentText(e.getMessage());

	            e.printStackTrace();
	            alert.showAndWait();
	        }
		}
		else {
			InputStream inputStream = null;
	        HSSFWorkbook workBook = null;
	        //XSSFWorkbook workBook = null;
	        try {
	            inputStream = new FileInputStream(fileName);
	            workBook = new HSSFWorkbook(inputStream);
	            //workBook = new XSSFWorkbook(inputStream);
	            for (int i = 0; i < workBook.getNumberOfSheets(); i++)
	            	arr.add(workBook.getSheetAt(i).getSheetName());
	        } catch (IOException e) {
	        	Alert alert = new Alert(AlertType.ERROR);
	        	alert.setTitle("Error Dialog");
	        	alert.setHeaderText("Ooops, there was an error!");
	        	alert.setContentText(e.getMessage());

	            e.printStackTrace();
	            alert.showAndWait();
	        }
		}
		return arr;
	}
	
public  ArrayList<String> parseToArr(String fileName, String sheetName) {
	    
		
		if (fileName.charAt(fileName.length()-1) == 'x') {
			return parceXlsx(fileName, sheetName);
		}
		else {
			return parceXls(fileName, sheetName);
		}
	        
	    }
	
	
	public  String parseToHtm(String fileName, String sheetName) {
	    
		
		if (fileName.charAt(fileName.length()-1) == 'x') {
			return parceXlsxToHtm(fileName, sheetName);
		}
		else {
			return parceXlsToHtm(fileName, sheetName);
		}
	        
	    }
}
