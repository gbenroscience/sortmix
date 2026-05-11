/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.github.gbenroscience.sortmix.bucketsorttunedquicksort; 



import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author JIBOYE, OLUWAGBEMIRO OLAOLUWA
 * 
 */
public class Test {
  
    /**
     *
     * @param parent An array of strings.
     * @param word A word.
     * @param bonuses The available bonuses
     * @return true if, based on available word bonuses,
     * <code>word</code> can be formed from the letters in <code>parent</code>.
     */
    public static boolean canFormWithWordBonusEdits( String[] parent,String word, int bonuses){
        Random rnd = new Random();
        int bonusLetterEdits = bonuses;

        boolean useAllBonuses = rnd.nextBoolean();

        if(!useAllBonuses){
            /**
             * Try to discourage RobotPlayers from using up all their
             * bonuses at once.
             */
            if(bonusLetterEdits>0){
                bonusLetterEdits = 1 + rnd.nextInt( bonusLetterEdits );
            }//end if
        }//end if
        System.out.println("Approved bonuses: "+bonusLetterEdits);
        /**
         * Will contain the letters not found in parent.
         */
        List<String>missingLettersList = new ArrayList<String>();
       
        
String[]duplicateParent = new String[parent.length];
System.arraycopy(parent, 0, duplicateParent, 0, parent.length);
int len = word.length(); 
        
        for(int j=0;j<len;j++) {
            boolean found = false;
            String inputLetter = word.substring(j,j+1);
            for (int i = 0; i < duplicateParent.length; i++) {
    
     if(duplicateParent[i] != null  && inputLetter.equalsIgnoreCase(duplicateParent[i]) ){
          found = true;
        duplicateParent[i] = null; 
        break;
    }
            }
            
            if(!found){
                missingLettersList.add(inputLetter);
            }
        }

        return missingLettersList.size() <= bonusLetterEdits;
    }
    /**
     *
     * @param parent An array of strings.
     * @param word A word.
     * @return true if <code>word</code> can be formed from the letters in <code>parent</code>.
     */
    public static boolean conts( String[] parent,String word){
       
         
int len = word.length();
String[]duplicateParent = new String[parent.length];
System.arraycopy(parent, 0, duplicateParent, 0, parent.length);
        int count = 0;
        for(int j=0;j<len;j++) {
            String inputLetter = word.substring(j,j+1);
            for (int i = 0; i < duplicateParent.length; i++) {
    if(duplicateParent[i] != null  && inputLetter.equalsIgnoreCase(duplicateParent[i]) ){
        count++;
        duplicateParent[i] = null;
        if(count == len && !word.isEmpty()){
            return true;
        }
        break;
    }
            }
        }


//  System.out.println(userName.matches("^[A-Za-z0-9_-]{3,15}$"));
return count == len && !word.isEmpty();//if count == len, then all letters in the word are present in the main array(i.e. parent)

    }
    
    public static void testArray(){
          
        int TEST_SIZE = 30000000;
        System.out.println(">Creating List...");
        
        int[] data = new int[TEST_SIZE + 4];
         
        
        
        String listType = data.getClass().getSimpleName();
        
           System.out.println(">Created "+listType);
         double testVal = 0;
        for(int i=0;i<100000;i++){
          testVal += Math.sin(i);
        }
        System.out.println("testVal: "+testVal);
        Random r = new Random();
        data[0] = r.nextInt(1000000);
        data[1] = r.nextInt(1000000);
        data[2] = r.nextInt(1000000);
        data[3] = r.nextInt(1000000);
        
        
           System.out.println(">"+listType+" will be filled with "+TEST_SIZE+" items");
        double start = System.nanoTime();
        
        for(int i=4;i<TEST_SIZE+4;i++){
          data[i] = r.nextInt(1000000);
        }
        double duration = (System.nanoTime() - start)/1000000;
        
           System.out.println(listType+" filled");
        
        System.out.println("Duration for adding "+TEST_SIZE+" items to "+listType+" is "+duration+" ms");
        
           System.out.println("Element search begins---going to index..."+ (data.length/2));
        duration = System.nanoTime();
        int elem = data[data.length/2];
        duration = (System.nanoTime() - duration)/1000000;
          System.out.println("Duration for getting element ("+elem+ ") at index ("+(data.length/2)+") in "+listType+" is "+duration+" ms");
      
           System.out.println("Element search begins---going to index...5000000");
            duration = System.nanoTime();
        elem = data[5000000];
        duration = (System.nanoTime() - duration)/1000000;
           System.out.println("Duration for getting element ("+elem+ ") at index (5000000) in "+listType+" is "+duration+" ms");
           
       System.out.println("Element search begins---goint to index..."+ ( (int)(data.length * 0.8) ) );
            duration = System.nanoTime();
              elem = data[ (int)(data.length * 0.8)];
        duration = (System.nanoTime() - duration)/1000000;
             System.out.println("Duration for getting element ("+elem+ ") at index ("+((int)(data.length * 0.8))+") in "+listType+" is "+duration+" ms");
      
        
    }
    
    
    public static void testLinkedList(){
          
        int TEST_SIZE = 30000000;
        System.out.println(">Creating List...");
        LinkedList<Integer>data = new LinkedList<>();
        
        
        String listType = data.getClass().getSimpleName();
        
           System.out.println(">Created "+listType);
         double testVal = 0;
        for(int i=0;i<100000;i++){
          testVal += Math.sin(i);
        }
        System.out.println("testVal: "+testVal);
        Random r = new Random();
        data.add(r.nextInt(1000000));
        data.add(r.nextInt(1000000));
        data.add(r.nextInt(1000000));
        data.add(r.nextInt(1000000));
        
           System.out.println(">"+listType+" will be filled with "+TEST_SIZE+" items");
        double start = System.nanoTime();
        
        for(int i=0;i<TEST_SIZE;i++){
          data.add(r.nextInt(1000000));
        }
        double duration = (System.nanoTime() - start)/1000000;
        
           System.out.println(listType+" filled");
        
        System.out.println("Duration for adding "+TEST_SIZE+" items to "+listType+" is "+duration+" ms");
        
           System.out.println("Element search begins---going to index..."+data.size()/2);
        duration = System.nanoTime();
        int elem = data.get(data.size()/2);
        duration = (System.nanoTime() - duration)/1000000;
          System.out.println("Duration for getting element ("+elem+ ") at index ("+data.size()/2+") in "+listType+" is "+duration+" ms");
      
           System.out.println("Element search begins---going to index...5000000");
            duration = System.nanoTime();
        elem = data.get(5000000);
        duration = (System.nanoTime() - duration)/1000000;
           System.out.println("Duration for getting element ("+elem+ ") at index (5000000) in "+listType+" is "+duration+" ms");
           
       System.out.println("Element search begins---goint to index..."+ ( (int)(data.size() * 0.8) ) );
            duration = System.nanoTime();
              elem = data.get( (int)(data.size() * 0.8) );
        duration = (System.nanoTime() - duration)/1000000;
             System.out.println("Duration for getting element ("+elem+ ") at index ("+((int)(data.size() * 0.8))+") in "+listType+" is "+duration+" ms");
      
        
    }
    
    public static void testArrayList(){
          
        int TEST_SIZE = 30000000;
        System.out.println(">Creating List...");
        ArrayList<Integer>data = new ArrayList<>();
        
        
        String listType = data.getClass().getSimpleName();
        
           System.out.println(">Created "+listType);
         double testVal = 0;
        for(int i=0;i<100000;i++){
          testVal += Math.sin(i);
        }
        System.out.println("testVal: "+testVal);
        Random r = new Random();
        data.add(r.nextInt(1000000));
        data.add(r.nextInt(1000000));
        data.add(r.nextInt(1000000));
        data.add(r.nextInt(1000000));
        
           System.out.println(">"+listType+" will be filled with "+TEST_SIZE+" items");
        double start = System.nanoTime();
        
        for(int i=0;i<TEST_SIZE;i++){
          data.add(r.nextInt(1000000));
        }
        double duration = (System.nanoTime() - start)/1000000;
        
           System.out.println(listType+" filled");
        
        System.out.println("Duration for adding "+TEST_SIZE+" items to "+listType+" is "+duration+" ms");
        
           System.out.println("Element search begins---going to index..."+data.size()/2);
        duration = System.nanoTime();
        int elem = data.get(data.size()/2);
        duration = (System.nanoTime() - duration)/1000000;
          System.out.println("Duration for getting element ("+elem+ ") at index ("+data.size()/2+") in "+listType+" is "+duration+" ms");
      
           System.out.println("Element search begins---going to index...5000000");
            duration = System.nanoTime();
        elem = data.get(5000000);
        duration = (System.nanoTime() - duration)/1000000;
           System.out.println("Duration for getting element ("+elem+ ") at index (5000000) in "+listType+" is "+duration+" ms");
           
       System.out.println("Element search begins---goint to index..."+ ( (int)(data.size() * 0.8) ) );
            duration = System.nanoTime();
              elem = data.get( (int)(data.size() * 0.8) );
        duration = (System.nanoTime() - duration)/1000000;
             System.out.println("Duration for getting element ("+elem+ ") at index ("+((int)(data.size() * 0.8))+") in "+listType+" is "+duration+" ms");
      
        
    }
    
    
    public static void main(String[] args) {
        
        
          testLinkedList();
          
          testArrayList();
          
          testArray();
          
        
  
        
        
        
        
        
        
      //String userName = "gbenro-myinven_tions";
         
        //System.out.println(userName.matches("^[A-Za-z]+[A-Za-z0-9_-]{3,20}$"));
        
        /*
        String data = "ABCDGHTERIOPQYZSGF";
        List<String> first = new ArrayList<>(Arrays.asList(data.split("")));
        List<String> second = new ArrayList<>(Arrays.asList(data.split("")));
        
        System.out.println("first: "+first);
        System.out.println("second: "+second);
        first.removeAll(second);
        
        System.out.println("AFTER REMOVAL:");
        System.out.println("first: "+first);
        System.out.println("second: "+second);
                */
       /* class Box{
            int length;
            int breadth;
            int height;

            public Box(int length, int breadth, int height) {
                this.length = length;
                this.breadth = breadth;
                this.height = height;
            }
            
            
        }
        
        Box a = new Box(3,4,5);
        Box b = new Box(12,32,5);
        Box c = new Box(2,320,59);
        Box d = new Box(1,302,656);
        Box e = new Box(102,132,45);
        
        Box a1 = new Box(3,4,5);
        Box b1 = new Box(12,32,5);
        Box c1 = new Box(2,320,59);
        Box d1 = new Box(1,302,656);
        Box e1 = new Box(102,132,45);
        
        List<Box> first = new ArrayList<>(Arrays.asList(new Box[]{a,b,c,d,e}));
        List<Box> second = new ArrayList<>(Arrays.asList(new Box[]{a1,b1,c1,d1,e1}));
        
        
        
         System.out.println("first: "+first);
        System.out.println("second: "+second);
        first.removeAll(second);
        
        System.out.println("AFTER REMOVAL:");
        System.out.println("first: "+first);
        System.out.println("second: "+second);
        
        
        int len = 100;
        
        int index = new Random().nextInt(len);
        
        if(index < (len >> 1)){
            System.out.println("len >> 1:           "+ (len >> 1)+", index: "+index);
        }
        else{
             System.out.println("..............len >> 1:           "+ (len >> 1)+", index: "+index);
        }
        
        
        for(int i=10000;i > 0;i-=80){
             System.out.println(i +" >> 2 = " + (i >> 2) );
        }
        */
        
    }
}
