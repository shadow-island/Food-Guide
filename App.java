package Guide;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Scanner;
import java.util.Set;
import java.util.SortedMap;
import java.util.TreeMap;

final class App {
    public static void main(final String[] args)  {
    	System.out.println("Eugene's Java Program Start");
    	
    	final MainClass mainclass = new MainClass();
    	mainclass.run();
    	
    	System.out.println("Program End");
    }
}

// made this class to use private method as main class can't
final class MainClass {    
	final public void run()  {
		//1. making table
    	// we can change just these based on raw data    	

    	// GENDER_CSV_TAB, AGE_CSV_TAB FOODGROUP_CSV_TAB SERVING_CSV_TAB
    	final List<Integer> servingsPerDayColumns = Arrays.asList(1, 2, 0, 3);    	
    	final List<List<String>> servingsPerDayTable = makeTable("servings_per_day-en_ONPP.csv", servingsPerDayColumns);
    	
    	// FOODGROUP_CSV_TAB, SERVINGSIZE_CSV_TAB, FOODNAME_CSV_TAB
    	final List<Integer> foodColumns = Arrays.asList(0, 2, 3);    	
    	final List<List<String>> foodTable = makeTable("foods-en_ONPP_rev.csv", foodColumns);
    	
    	// ID_CSV_TAB, NAME_CSV_TAB 
    	final List<Integer> foodgroupColumns = Arrays.asList(0, 1);    	
    	final List<List<String>> foodgroupTable = makeTable("foodgroups-en_ONPP.csv", foodgroupColumns);
    	   	    	    	
        // 2. Making 2 menus automatically(genderMenu, ageMenu)
    	// and getting Input    	
    	final Set<String> genderSet = new HashSet<String>();
    	final Set<String> ageSet = new HashSet<String>();        
        for (List<String> record : servingsPerDayTable) {
        	// when using set, trim will be done, so we need it before
        	genderSet.add(record.get(0));
        	ageSet.add(record.get(1)); 
        }        
        
	    final Scanner sannerInput = new Scanner(System.in);
    	final String inputGender = getInput(sannerInput,makeGenderMenu(genderSet));
    	final String inputAge = getInput(sannerInput,makeAgeMenu(ageSet));
    	    	
    	// 3. search and show
    	while (true) {
    		show(inputGender, inputAge, servingsPerDayTable, foodTable, foodgroupTable);
    		        	
        	System.out.println("\nDo you want alternative lists? 'y'(or any key) / 'n'");        	   
        	if (sannerInput.nextLine().equals("n")) {
        		break;
        	} 
    	}    	
    	sannerInput.close();
    }
			
	final private List<String> makeGenderMenu(final Set<String> genderSet) {
		final List<String> genderMenu = new ArrayList<>(genderSet);
		// to show always with the same order
		Collections.sort(genderMenu);
		Collections.reverse(genderMenu);  
		
		return genderMenu;
	}
	
	final private List<String> makeAgeMenu(final Set<String> ageSet) {
		final List<String> ageMenu = new ArrayList<>(); 
		final SortedMap<Integer, String> sm = new TreeMap<Integer, String>();        
		for (String s : ageSet) {
			final String[] parts = s.split(" ");
			String first_word = parts[0];	 
			first_word = first_word.replace("+", "");	 	
				sm.put(Integer.parseInt(first_word), s);	    	
		}  
		
		for (Map.Entry<Integer, String> entry : sm.entrySet()) {
			ageMenu.add(entry.getValue());
		}
		return ageMenu;
	}
	
	final private void show(	final String inputGender, final String inputAge, 
						List<List<String>> servingsPerDayTable, 
						List<List<String>> foodTable, 
						List<List<String>> foodgroupTable) {
    	System.out.println("This is random lists that fulfill this criteria:");
    	
    	for (List<String> record : servingsPerDayTable) {
    		if (record.get(0).equals(inputGender) && record.get(1).equals(inputAge)) {
    			System.out.println("----------------------------");
    			
    			final String foodGroupId = record.get(2);
    			
    			String foodGroupName = "";    			
    			for (List<String> foodGroupRecord : foodgroupTable) {
    				
    				if (foodGroupRecord.get(0).equals(foodGroupId)) {
    					foodGroupName = foodGroupRecord.get(1);
    					break;
    				}
    			}    			
    			System.out.println(foodGroupName);        			
    			System.out.println("SERVINGS/DAY:" + record.get(3));
    			
    			List<String> foodCandiates = new ArrayList<>();
    			for (List<String> f : foodTable)  {
    				if (f.get(0).equals(foodGroupId)) {
    					foodCandiates.add(f.get(1) + " " + f.get(2));
    				}
    			}
    			
    			final Random rnd = new Random();
    			final int r = rnd.nextInt(foodCandiates.size());
    			System.out.println("FOOD EXAMPLE:" + foodCandiates.get(r));
    		}
    	}
    }
		   
	final private List<List<String>> makeTable(final String csvFile, final List<Integer> columns)  {
		List<List<String>> returnTable = new ArrayList<>();
    	
        Scanner scanner;
		try {
			scanner = new Scanner(new File(csvFile));
		    int lineNum = 0;
	        while (scanner.hasNext()) {	
	            List<String> line = parseCSVLine(scanner.nextLine());
	                    
	        	//skip the first line it's for column names
	        	if (++lineNum == 1) {
	        		continue;
	        	}       
	        	
	            List<String> record = new ArrayList<>();
	            for (int i = 0 ; i < columns.size() ; i++) {
	            	String item = line.get(columns.get(i));
	            	item = item.trim();
	            	record.add(item);
	            }
	            
	            returnTable.add(record);
	        }
	        scanner.close();
	        
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
        
        return returnTable;
	}
    
	final private List<String> parseCSVLine(String csvLine) {
    	final char DEFAULT_SEPARATOR = ',';
        final char DEFAULT_QUOTE = '"';
    
    	char separators = DEFAULT_SEPARATOR;
    	char customQuote = DEFAULT_QUOTE;
    	
        List<String> result = new ArrayList<>();

        //if empty, return!
        if (csvLine == null || csvLine.isEmpty()) {
            return result;
        }

        if (customQuote == ' ') {
            customQuote = DEFAULT_QUOTE;
        }

        if (separators == ' ') {
            separators = DEFAULT_SEPARATOR;
        }

        StringBuffer curVal = new StringBuffer();
        boolean inQuotes = false;
        boolean startCollectChar = false;
        boolean doubleQuotesInColumn = false;

        char[] chars = csvLine.toCharArray();

        for (char ch : chars) {
            if (inQuotes) {
                startCollectChar = true;
                if (ch == customQuote) {
                    inQuotes = false;
                    doubleQuotesInColumn = false;
                } else {
                    // allow "" in custom quote enclosed
                    if (ch == '\"') {
                        if (!doubleQuotesInColumn) {
                            curVal.append(ch);
                            doubleQuotesInColumn = true;
                        }
                    } else {
                        curVal.append(ch);
                    }
                }
            } else {
                if (ch == customQuote) {
                    inQuotes = true;
                    // allow "" in empty quote enclosed
                    if (chars[0] != '"' && customQuote == '\"') {
                        curVal.append('"');
                    }
                    //double quotes in column will hit this!
                    if (startCollectChar) {
                        curVal.append('"');
                    }

                } else if (ch == separators) {
                    result.add(curVal.toString());
                    curVal = new StringBuffer();
                    startCollectChar = false;
                } else if (ch == '\r') {
                    //ignore LF characters
                    continue;
                } else if (ch == '\n') {
                    //the end, break!
                    break;
                } else {
                    curVal.append(ch);
                }
            }

        }

        result.add(curVal.toString());
        return result;
    }
    
	final private String getInput(Scanner in, List<String> menuList) {
    	final String returnKey;
    	
	    for (int i = 0; i < menuList.size(); i++) {
	    	System.out.println(i+1 + ":" + menuList.get(i));	   
	    }	    
	    System.out.println("Enter a number.");
		
    	final int index = in.nextInt() - 1;
    
    	returnKey = menuList.get(index);
    	System.out.println("'" +returnKey + "' has been selected");
    	return returnKey;
    }
}
