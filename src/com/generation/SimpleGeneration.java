package com.generation;

import java.util.*;

import com.common.Sent;
import com.util.Constants;

/**  
 * TODO: Create classes (more OOP) 
 * 		 Transform to 'NO' questions
 * 		 Sentence compression
 **/
class SimpleGeneration {
  public static void main(String[] args) {
    
    System.out.println("Please input the sentence: ");
    
    Scanner sc = new Scanner(System.in);
    
    String str = sc.nextLine();
    
    QuestionGenerator gt = QuestionGeneratorFactory.makeGenerator("SimpleGenerator");
    
    Constants.init();
    
    Sent input = new Sent();
    
    input.init(str);
    
    String ret = gt.generateSent(input);
    
    System.out.println(ret);
    
  }
}
