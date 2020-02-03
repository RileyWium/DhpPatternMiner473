//import java.io.BufferedReader;
//import java.io.BufferedWriter;
import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileInputStream;
//import java.io.FileReader;
//import java.io.FileWriter;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Scanner;

import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.filechooser.FileNameExtensionFilter;

public class DHPproj {
	
	public static void main(String[] args) {
		//works with databases that don't have items in order
		DHPproj runner = new DHPproj();
		//start timer
		/*
		long startTime = System.currentTimeMillis();
		runner.dhpWithDBTriming(args);
		long stopTime = System.currentTimeMillis();
	    long elapsedTime = stopTime - startTime;
	    System.out.println(elapsedTime);
		*/
		runner.gui();
		
	}
	public void gui(){

		GridLayout gLay =new GridLayout(6,1);
		JFrame guiFrame = new JFrame();
		guiFrame.setLayout(gLay);
		//make sure the program exits when the frame closes
		guiFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		guiFrame.setTitle("DHP");
		guiFrame.setSize(350,250);
		//This will center the JFrame in the middle of the screen
		guiFrame.setLocationRelativeTo(null);
		//Options for the JList
		//The first JPanel contains a JLabel and JCombobox
		final JPanel supPanel = new JPanel();
		JLabel supLbl = new JLabel("Min Support Threshold Percent:");
		supPanel.add(supLbl);
		JTextField supField = new JTextField("50.0",3);
		supPanel.add(supField);
		final JPanel hashPanel = new JPanel();
		JLabel hashLbl = new JLabel("Num of Hash Buckets:");
		hashPanel.add(hashLbl);
		JTextField bucketField = new JTextField("7",7);
		hashPanel.add(bucketField);
		//Create the second JPanel. Add a JLabel and JList and
		//make use the JPanel is not visible.
		final JPanel pPanel = new JPanel();
		JLabel pathLabel = new JLabel("");
		pPanel.add(pathLabel);		
		JTextField namedOutput = new JTextField("Name_of_Output_file",15);
		JButton compileBtn = new JButton("Run DHP");
		compileBtn.addActionListener(new ActionListener()
		{
			@Override
			public void actionPerformed(ActionEvent event)
			{
				//int tmp =Integer.parseInt(textField.getText());
				//String error ="";
				String[] args = new String[4];
				
				args[0] = pathLabel.getText();
 				
				args[1] = supField.getText();
				
				args[2] = namedOutput.getText();
				
				args[3] = bucketField.getText();
				dhp(args);
			}
		});
		final JPanel listPanel = new JPanel();
		JLabel inputField = new JLabel("Name of File:");
		listPanel.add(inputField);
		
		JButton importBtn = new JButton("import");
		importBtn.addActionListener(new ActionListener()
		{
			@Override
			public void actionPerformed(ActionEvent event)
			{
				String cf = "";
				JFileChooser chooser = new JFileChooser();
		        FileNameExtensionFilter filter = new FileNameExtensionFilter(
		                "JPG & GIF Images", "jpg", "gif");
		        chooser.setFileFilter(filter);
		        int returnVal = chooser.showOpenDialog(null);
		        if(returnVal == JFileChooser.APPROVE_OPTION) {
		            cf = chooser.getSelectedFile().getName();
		        }
		        String tmp = "Name of File: "+cf;
		        inputField.setText(tmp);
		        
		        pathLabel.setText(chooser.getSelectedFile().getPath());
		        
			}
		});
		listPanel.add(importBtn);
		
		final JPanel midPanel = new JPanel();
		
		JLabel littleLabel = new JLabel(".txt");
		midPanel.add(namedOutput);
		midPanel.add(littleLabel);
		
		//final JPanel nPanel = new JPanel();
		/*
		nPanel.add(comboPanel, BorderLayout.NORTH);
		nPanel.add(listPanel, BorderLayout.CENTER);
		nPanel.add(pPanel,BorderLayout.SOUTH);
		
		final JPanel sPanel = new JPanel();
		sPanel.add(midPanel);
		sPanel.add(compileBtn);
		*/
		//The JFrame uses the BorderLayout layout manager.
		//Put the two JPanels and JButton in different areas.
		guiFrame.add(supPanel);
		guiFrame.add(hashPanel);
		guiFrame.add(listPanel);
		guiFrame.add(pPanel);
		guiFrame.add(midPanel);
		guiFrame.add(compileBtn);
		//guiFrame.add(nPanel);
		//guiFrame.add(sPanel,BorderLayout.SOUTH);
		//make sure the JFrame is visible
		guiFrame.setVisible(true);
	
	}
	public void dhp(String[] args) {
		long startTime = System.currentTimeMillis();
		double minSupp = 0;
		int bucketNum = Integer.parseInt(args[3]);
	
		File rawData = new File(args[0]);
		FileInputStream fis = null;
		try{
			 fis = new FileInputStream(rawData);
		}catch (Exception e) {
			// TODO: handle exception
			System.out.println(e);
		}
		Scanner scanFile = new Scanner(fis);
		int dataRows = Integer.parseInt(scanFile.nextLine());
        
		double minSup = Double.parseDouble(args[1]);
		if(minSup <0 || minSup > 100){
			System.out.println("The minimum support threshold must be between 0 and 100%.");
			System.exit(0);
		}
		minSupp = Math.rint(dataRows*minSup/100);
		
		//fList is going to store count of all distinct items in database to be then pruned for f1
		HashMap<Integer, Integer> fList = new HashMap<Integer,Integer>();
		ArrayList<int[]> f1 = new ArrayList<int[]>();
		ArrayList<PatternSup> answer = new ArrayList<PatternSup>();
		//F1 is currently null
		
		//H2 is hash table for 2-itemsets 
		HashTable h2 = new HashTable(2,bucketNum);
		//read transactions and count 
		//appearances of each item, generate H2
		ArrayList<boolean[]> bFreqTracker = new ArrayList<boolean[]>();
		
		for(int i=0; i< dataRows; i++){
			
			String line = scanFile.nextLine();

			Scanner scanLine = new Scanner(line);
			int[] transaction = null;
			boolean[] bTrack = null;
			int transLength = 0; 
			  
			int lineCntr = 0;
			while (scanLine.hasNextInt()) {
				if(lineCntr ==0){
					  //do nothing
					scanLine.nextInt();
				}else if(lineCntr ==1){
					transLength = scanLine.nextInt();
					transaction = new int[transLength];
					bTrack = new boolean[transLength+1];
					bTrack[0] = true;
				}else{
					int item = scanLine.nextInt();
					if(fList.containsKey(item)){
						fList.put(item, fList.get(item).intValue()+1);
					}else{
						fList.put(item, 1);
					}
					bTrack[lineCntr-1] = true;
					transaction[lineCntr-2] = item;
				}
				lineCntr++;
			  // do whatever needs to be done with token
			}
			scanLine.close();
			bFreqTracker.add(bTrack);
					
			//for each 2-itemset in transaction
			ArrayList<int[]> twoKItemset = formKItemset(transaction, 2);
			for(int[] item : twoKItemset){
				h2.addEntry(item);
			}
		}
		scanFile.close();
		//br.close();
		//form set of frequent l-itemsets
					
		for(Integer i : fList.keySet()){
			if(fList.get(i) >= minSupp){
				int[] temp = {i};
				f1.add(temp);
				answer.add(new PatternSup(temp, fList.get(i)));
				//System.out.println("f1 added: "+i+" sup: "+fList.get(i));
			}
		}
		//remove hash values without min sup;
		h2.prune(minSupp);
		//find Fk, k>=2
		ArrayList<int[]> prevF = f1;
		HashTable currentHT = h2;
		
		int k = 2;
		while(prevF.size() >0){
			
			ArrayList<PatternSup> ck = new ArrayList<PatternSup>();

			//makes all combinations of Fk-1 * Fk-1
			for(int i = 0; i< prevF.size(); i++){
				int[] lookingArray = prevF.get(i);
				for(int j = i+1; j< prevF.size(); j++){
					boolean skip = false;
					for(int g = 0; g < (k-1); g++){
						//System.out.println("starting g");
						if(g == (k-2)){
							//at end of array, make candidate
							int[] newCan = new int[k];
							for(int h =0; h < lookingArray.length; h++){
								newCan[h] = lookingArray[h];
							}
							newCan[k-1] = prevF.get(j)[g];
							//now check if new candidate has min support
							if(currentHT.hasSupp(newCan)){
								ck.add(new PatternSup(newCan, 0));
							}
							
						}else{
							if(lookingArray[g] != prevF.get(j)[g]){
								skip = true;
								break;
							}
						}
					}
					if(skip) break;
				} 
			}
			
			//Scanner fileReader = new Scanner(rawData);
			try{
				fis = new FileInputStream(rawData);
			}catch (Exception e) {
				// TODO: handle exception
				System.out.println(e);
			}
			Scanner fileScanner = new Scanner(fis);
			fileScanner.nextLine();//skip first line we already have dataRows
			
	        HashTable kPlusHT = new HashTable((k+1),bucketNum);
	        //int modifiedDataRows = dataRows;
	        
			for(int i=0; i< dataRows; i++){
				
				String currentReadingLine = fileScanner.nextLine();
				HashMap<Integer,Integer> itemSupCntr = new HashMap<Integer,Integer>();

				Scanner lineScanner = new Scanner(currentReadingLine);
				int[] transaction = null;
				boolean[] bTracking = null;
				int validItemSize = 0;
				int lineCntr = 0;
				int trimedCnt = 0;
				//int tCntr = 0;
				//we use this to trim down database transactions
				boolean tEmpty = false;
				while (lineScanner.hasNextInt()) {
					if(lineCntr ==0){
						  //do nothing
						bTracking = bFreqTracker.get(lineScanner.nextInt()-1);
						
					}else if(lineCntr ==1){
						if(bTracking[0] == false){
							tEmpty = true;
							break;								
						}
						transaction = new int[lineScanner.nextInt()];
					}else{
						int item = lineScanner.nextInt();
						if(bTracking[lineCntr-1]){
							transaction[lineCntr-(2+trimedCnt)] = item;
							validItemSize++;
							//map items in transaction for future trimming
							itemSupCntr.put(item, 0);
						}else{
							trimedCnt++;
						}							
					}
					lineCntr++;
				}
				lineScanner.close();
				if(!tEmpty){
					transaction = Arrays.copyOfRange(transaction,0,validItemSize);
					/*
					System.out.println("here are the transactions:");
					for(int tx : transaction){
						System.out.print(tx+", ");
					}
					System.out.println();
					*/
					
					//ArrayList<int[]> kItemset = formKItemset(transaction, k);
					int tCntr = 0;
					
					for(PatternSup pS : ck){
						boolean goodPat = true;
						for(int ps : pS.array){
							boolean inTrans =false;
							for(int p = 0; p < transaction.length; p++){
								if(ps == transaction[p]){
									inTrans = true;
								}
							}
							if(!inTrans){
								goodPat = false;
								break;
							}								
						}
						if(goodPat){
							pS.sup += 1;
							tCntr++;
						}
						//tCntr
					}
					
					
					/*
					for(int[] kItm : kItemset){
						Arrays.sort(kItm);//**may not be needed, also check this to see if we can implement .hasSup here
						for(PatternSup ps : ck){
							if(cmpIntArry(kItm, ps.array)){
								ps.sup += 1;
								tCntr++;
								//maybe able to trim this by skipping if first items don't match, because of sorting **
							}
						}
					}
					*/
					//if their are at least k+1 valid itemsets in transaction then don't remove transaction
					if(tCntr >= (k+1)){	
						
						ArrayList<int[]> kPlusItemset = formKItemset(transaction, (k+1));
						
						for(int[] kPlsItm: kPlusItemset){
							
							boolean allSubset =true;
							int[] kSub = new int[k];
							for(int x = 0; x < (k+1) ; x++){
								int cntr = 0;
								for(int y =0 ; y < (k+1); y++){
									if(y != x){
										kSub[cntr] = kPlsItm[y];
										cntr++;
									}
								}
								/*
								if(!currentHT.hasSupp(kSub)){
									allSubset = false;
									break;
								}*/
								
								if(currentHT.hasSupp(kSub)){
									for(int subItem : kSub){
										//** maybe some small enhancement, probs better off implementing mutable int
										itemSupCntr.put(subItem, itemSupCntr.get(subItem)+1);
									}
								}else{
									allSubset = false;
									break;
								}
							}
							if(allSubset){
								kPlusHT.addEntry(kPlsItm);
							}		
						}		
						//trim database
						//if item support counter didn't have high enough support
						for(int t = 0; t < transaction.length; t++){
							if(itemSupCntr.get(transaction[t]) < k){
								bTracking[t+1] = false;
							}
						}
						
					}else{
						//one less transaction to look at in the future 
						//and current transaction not added to modifiedFileContent
						bTracking[0] = false;
						//modifiedDataRows -= 1;
					}
				}
			}
							
			//dataRows = modifiedDataRows; 
			fileScanner.close();
			
			//Fk = null // the set of frequent k-itemset
			prevF = new ArrayList<int[]>();
			
			//Answer += Fk;
			for(PatternSup ps : ck){
				if(ps.sup >= minSupp){
					prevF.add(ps.array);
					answer.add(ps);
				}
			}
			//remove the hash values without the minimum support from Hk+1
			currentHT = kPlusHT;
			currentHT.prune(minSupp);
			
			k++;
		}
		//print answer
		LinkedList<String> outputList = new LinkedList<String>();
		for(PatternSup x: answer){
			String outputLine = "";
			for(int y: x.array){
				outputLine += (y+", ");
			}
			outputLine += (": "+x.sup);
			outputList.add(outputLine);
		}
		long stopTime = System.currentTimeMillis();
	    long elapsedTime = stopTime - startTime;
		String freqPatString = "|FP| = "+Integer.toString(answer.size());
		System.out.println(freqPatString);
		outputList.addFirst(freqPatString);
		outputList.addFirst("Time taken in milliseconds: "+elapsedTime);
		Path file = Paths.get(args[2]+".txt");
		try{
			Files.write(file, outputList, Charset.forName("UTF-8"));
			System.out.println("succeeded in writing to: "+file.toAbsolutePath());
		}catch (Exception e) {
			// TODO: handle exception
			System.out.println(e);
		}
	}
	//this compares int arrays with no repeats, of the same size, and in sorted order, 
	public boolean cmpIntArry(int[] x, int[] y){
		//boolean areEqual = true;
		if(x.length != y.length){return false;}
		for(int i = 0; i < y.length;i++){
			if (x[i] != y[i]) {return false;}
		}			
		return true;
	}
	public ArrayList<int[]> formKItemset(int[] t, int k){
		ArrayList<int[]> comb = new ArrayList<int[]>();
		//turn string[] to int[]
		int[] input = t.clone();
		/*
		int[] input = new int[t.length];
		for(int i = 0; i < t.length; i++){
			input[i] = t[i];//if you want String[] parse this
		}
		*/
		int[] s = new int[k]; //tracks indices pointing to elements
		
		if(t.length >= k){
			// first index sequence: 0, 1, 2, ...
		    for (int i = 0; (s[i] = i) < k - 1; i++);  
		    comb.add(getSubset(input, s));
		    for(;;) {
		        int i;
		        // find position of item that can be incremented
		        for (i = k - 1; i >= 0 && s[i] == input.length - k + i; i--); 
		        if (i < 0) {
		            break;
		        }
		        s[i]++;                    // increment this item
		        for (++i; i < k; i++) {    // fill up remaining items
		            s[i] = s[i - 1] + 1; 
		        }
		        comb.add(getSubset(input, s));
		    }
		}
		return comb;
	}
	//generate combination from input by index sequence 
	public int[] getSubset(int[] input, int[] subset){
		int[] result = new int[subset.length];
		for(int i = 0; i < subset.length; i++) result[i] = input[subset[i]];
		return result;
	}
	public class PatternSup
	{
		public int[] array;
		public int sup;

	   public PatternSup(int[] a, int x)
	   {
	     array = a; sup = x;
	   }
	}
	class MutableInt {
		//causes concurrent write issues
		  int value = 1; // note that we start at 1 since we're counting
		  public MutableInt(int nValue){value = nValue;}
		  public void increment () { ++value;      }
		  public int  get ()       { return value; }
	}
	public class HashTable
	{
		//public ArrayList<Integer> bID;
		public ArrayList<Integer> itemCounter;
		//public ArrayList<HashSet<int[]>> patList;
		public int K;
		public int numBuckets = 1;
		//maybe should hold specific hash function
		//maybe hold a hash modifier that changes the hash function slightly
		
		public HashTable(int k, int buckets){
			K = k;
			numBuckets = buckets;
			//bID = new ArrayList<Integer>(buckets);
			itemCounter = new ArrayList<Integer>(buckets);
			for(int i =0; i < buckets; i++){itemCounter.add(0);}
			//patList = new ArrayList<HashSet<int[]>>(buckets);
			//for(int i =0; i < buckets; i++){patList.add(new HashSet<int[]>());}
		}
		/*
		public HashTable(ArrayList<Integer> bid, ArrayList<Integer> iCntr, ArrayList<ArrayList<int[]>> pList){
			bID = bid; itemCounter = iCntr; patList = pList;
		}
		*/
		//increments hash table counter and adds a single item or combination to hash table
		public void addEntry(int[] item){
			Arrays.sort(item);
			int category = hasher(item);
			itemCounter.set(category, itemCounter.get(category)+1);
		}
		public int hasher(int[] items){
			int product2mod = 0;
			for(int i =0; i< K; i++){
				product2mod += items[i]*(Math.pow(10,i));
			}
			return (product2mod % numBuckets);
		}
		public void prune(double minSup){
			//each transaction remove any
			for(int i = 0; i < numBuckets; i++){
				if(itemCounter.get(i) < minSup){
					//patList.get(i).clear();
					itemCounter.set(i, 0);
				}			
			}
		}
		public boolean hasSupp(int[] x){
			Arrays.sort(x);
			int category = hasher(x);
			if(itemCounter.get(category)>0){
				return true;
			}else{
				return false;
			}			
		}
		/*
		public boolean hasSupp(int[] x){
			Arrays.sort(x);
			int category = hasher(x);
			//System.out.println("hasSup got bucket: "+category);
			for(int[] itemset : patList.get(category)){
				if(cmpIntArry(x, itemset)== true) return true;
			}
			return false;
		}
		*/
	}
}
