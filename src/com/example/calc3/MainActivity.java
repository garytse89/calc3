package com.example.calc3;

import android.R.bool;
import android.os.Bundle;
import android.app.Activity;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;


// This is a primitive calculator application with some bugs.
// I implemented an original algorithm without any research or help, so the way the calculations are done and stored are perhaps very "primitive"
// For my comments: [...] implies a variable
// The word 'operator' means +, -, x, /, etc.

// The code is perhaps really confusing, so let me try to summarize as quickly as possible what happens when I enter:
// 8+2x5
// A string array called data[] becomes [ "8" | "+" | "2" | "x" | "5" ]
// operatorFlag is triggered because there is a multiplication or division (i.e. a priority operator)
// The array index of the multiplication operator is also returned, in this case "x" is at index = 3
// 2x5 is calculated first and stored into a double array called resultInter[]
// resultInter becomes [10] and is instantly transferred back into data[]
// data[] gets its multiplication operator and the original multiplying values replaced with 0's and the product, becoming [ "8" | "+" | "0"  | "10" | "0" ]
// Now we just use a for-loop to loop through the whole data[] index and either add or subtract;
// store this math into resultTemp (a double number that gets incremented by added or subtracted numbers) then convert it to string so the user can see the result.

// You can think of it as:
// if there is only addition or subtraction, then only data[] is looped through and the numbers incremented or decremented based on 
// a "+" or "-" sign, which is what I call a lower-level sequence (the code is simple and short)
// If there is multiplication or division (and a half-implemented bracket and exponent), then we evaluate those products first,
// throwing numbers back and forth between two arrays, until we end up replacing all the mul/div operator signs and end up with a casual expression.
// Then evaluate as a casual, lower-level addition/subtraction sequence afterwards.

public class MainActivity extends Activity implements OnClickListener {

	// Initialize the visual layout components: textview for displaying the numbers and operators, and the buttons (quite a few)
	TextView textResult, specialView;
	Button b0, b1, b2, b3, b4, b5, b6, b7, b8, b9, bAdd, bSub, bMul, bDiv, bC, bBrackets, bBack, bEqual, bDecimal, bPosNeg, bExponent;
	
	// Here comes a bunch of variables 
	static String data[] = new String[100];
	static int index = 0;
	
	static double resultNum = 0;
		static double resultTemp = 0; 
	static int bracketCount = 0;
	static String results = "";
	static int decimalFlag = 0;
	static double decimalCounter = 10;
	static int operatorAllowed = 0;
		static int operatorFlag = 0; 
	static int addFlag = 1; // by default, we do addition. 
	static int negFlag = 0;
	static int operatorPosition[] = new int[10];
	static double resultInter[] = new double[10];
		
	static int temp = 0;
	
		
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// "Initialize" the layout
		setContentView(R.layout.activity_main);
		// Link all the buttons
		b0 = (Button) findViewById(R.id.b0);
		b1 = (Button) findViewById(R.id.b1);
		b2 = (Button) findViewById(R.id.b2);
		b3 = (Button) findViewById(R.id.b3);
		b4 = (Button) findViewById(R.id.b4);
		b5 = (Button) findViewById(R.id.b5);
		b6 = (Button) findViewById(R.id.b6);
		b7 = (Button) findViewById(R.id.b7);
		b8 = (Button) findViewById(R.id.b8);
		b9 = (Button) findViewById(R.id.b9);
		bAdd = (Button) findViewById(R.id.bAdd);
		bSub = (Button) findViewById(R.id.bSub);
		bMul = (Button) findViewById(R.id.bMul);
		bDiv = (Button) findViewById(R.id.bDiv);
		bC = (Button) findViewById(R.id.bC);
		bBrackets = (Button) findViewById(R.id.bBrackets);
		bBack = (Button) findViewById(R.id.bBack);
		bEqual = (Button) findViewById(R.id.bEqual);
		bDecimal = (Button) findViewById(R.id.bDecimal);
		bPosNeg = (Button) findViewById(R.id.bPosNeg);
		bExponent = (Button) findViewById(R.id.bExponent);
		// the textview
		textResult = (TextView) findViewById(R.id.textResult);
		specialView = (TextView) findViewById(R.id.specialView);
		// Set all the buttons to eventually respond to clicks/presses
		b0.setOnClickListener(this); 
		b1.setOnClickListener(this); 
		b2.setOnClickListener(this); 
		b3.setOnClickListener(this); 
		b4.setOnClickListener(this); 
		b5.setOnClickListener(this); 
		b6.setOnClickListener(this); 
		b7.setOnClickListener(this); 
		b8.setOnClickListener(this); 
		b9.setOnClickListener(this); 
		bAdd.setOnClickListener(this); 
		bSub.setOnClickListener(this); 
		bMul.setOnClickListener(this); 
		bDiv.setOnClickListener(this); 
		bC.setOnClickListener(this); 
		bBrackets.setOnClickListener(this); 
		bBack.setOnClickListener(this); 
		bEqual.setOnClickListener(this); 
		bDecimal.setOnClickListener(this); 
		bPosNeg.setOnClickListener(this); 
		bExponent.setOnClickListener(this);
	}
	
	// No menus at the moment
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.activity_main, menu);
		return true;
	}

	// array set function
	public void set(int index, float value){
		
	}
	
	// The code where everything happens
	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		
		// There is a switch-case code for each button
		switch (v.getId()) {
		
			// Explanation for the code for number buttons 0-9:
			// The variable [results] is a STRING ARRAY. It is added to every time I press a button for a number or operator
		    // so that the user can see what he or she is inputting
		
			// [operatorAllowed] is a flag variable to prevent the user from pressing two operators in a row.
		    // as such, a valid mathematical expression can only contain a number, then an operator, then a number, and so forth.
		
			// [decimalFlag] is another flag variable for when I have inputted a decimal "." sign.
			// Logically, I cannot have multiple decimals in a single number, like 1.2.3 (not valid), so I have flags to prevent that
			// [decimalCounter] divides the inputted number accordingly based on how far behind the decimal point the number is
			// So for example, I enter the 3 in 1.23, by then decimalCounter would equal 100, so 3/100 = 0.03 added onto 1.20
		
			// [resultNum] is where all the math side of the application is done and this isn't shown to the user.
			// [resultNum] is reset to 0 everytime the user enters "C" (clear) or the equal sign more than once (this is a bug I've been to lazy to fit).
			// [resultNum] is multiplied by 10 and the number button pressed is incremented on top.
			
			// So for example, if I enter "8" and then "2" to try to get EIGHTY-TWO, the math that happens is:
			// resultNum = resultNum * 10 ===> 0
			// resultNum += 8 ===> 0+8=8
			// resultNum = resultNum * 10 ===> 8*10 = 80
			// resultNum += 2 ===> 80+2 = 82
		case R.id.b0:
				// Put into string array for BOTH display and later math conversions
				results += "0";
				operatorAllowed = 1;
				
				if( decimalFlag == 1 ){ // if there is a decimal in place, we need to do something different with the math
					decimalCounter *= 10;
				}
					// there can't be any decimals after an already decimal'd number, e.g. 9.2.5 can't exist
				else{
					resultNum *= 10;
					decimalFlag = 0;
				}
				
				break;
			case R.id.b1: 
				results += "1";
				operatorAllowed = 1;
				
				if( decimalFlag == 1 ){ // if there is a decimal in place, we need to do something different with the math
					resultNum += (1/decimalCounter); // decimalCounter is initialized at 10. So putting in "8.1" means 8 + 1/10 = 8.1
					decimalCounter *= 10;
					
				}
					// there can't be any decimals after an already decimal'd number, e.g. 9.2.5 can't exist
				else{
					resultNum *= 10;
					resultNum += 1;
					decimalFlag = 0;
				}
				break;
			case R.id.b2: 
				results += "2";
				operatorAllowed = 1;
				
				if( decimalFlag == 1 ){ // if there is a decimal in place, we need to do something different with the math
					resultNum += (2/decimalCounter); // decimalCounter is initialized at 10. So putting in "8.1" means 8 + 1/10 = 8.1
					decimalCounter *= 10;
					
				}
					// there can't be any decimals after an already decimal'd number, e.g. 9.2.5 can't exist
				else{
					resultNum *= 10;
					resultNum += 2;
					decimalFlag = 0;
				}
				break;
			case R.id.b3: 
				results += "3";
				operatorAllowed = 1;
				
				if( decimalFlag == 1 ){ // if there is a decimal in place, we need to do something different with the math
					resultNum += (3/decimalCounter); // decimalCounter is initialized at 10. So putting in "8.1" means 8 + 1/10 = 8.1
					decimalCounter *= 10;
					
				}
					// there can't be any decimals after an already decimal'd number, e.g. 9.2.5 can't exist
				else{
					resultNum *= 10;
					resultNum += 3;
					decimalFlag = 0;
				}
				break;
			case R.id.b4: 
				results += "4";
				operatorAllowed = 1;
				
				if( decimalFlag == 1 ){ // if there is a decimal in place, we need to do something different with the math
					resultNum += (4/decimalCounter); // decimalCounter is initialized at 10. So putting in "8.1" means 8 + 1/10 = 8.1
					decimalCounter *= 10;
					
				}
					// there can't be any decimals after an already decimal'd number, e.g. 9.2.5 can't exist
				else{
					resultNum *= 10;
					resultNum += 4;
					decimalFlag = 0;
				}
				break;
			case R.id.b5: 
				results += "5";
				operatorAllowed = 1;
				
				if( decimalFlag == 1 ){ // if there is a decimal in place, we need to do something different with the math
					resultNum += (5/decimalCounter); // decimalCounter is initialized at 10. So putting in "8.1" means 8 + 1/10 = 8.1
					decimalCounter *= 10;
					System.out.println("******"+resultNum);
					
				}
					// there can't be any decimals after an already decimal'd number, e.g. 9.2.5 can't exist
				else{
					resultNum *= 10;
					resultNum += 5;
					decimalFlag = 0;
				}
				break;
			case R.id.b6: 
				results += "6";
				operatorAllowed = 1;
				
				if( decimalFlag == 1 ){ // if there is a decimal in place, we need to do something different with the math
					resultNum += (6/decimalCounter); // decimalCounter is initialized at 10. So putting in "8.1" means 8 + 1/10 = 8.1
					decimalCounter *= 10;
					
				}
					// there can't be any decimals after an already decimal'd number, e.g. 9.2.5 can't exist
				else{
					resultNum *= 10;
					resultNum += 6;
					decimalFlag = 0;
				}
				break;
			case R.id.b7:
				results += "7";
				operatorAllowed = 1;
				
				if( decimalFlag == 1 ){ // if there is a decimal in place, we need to do something different with the math
					resultNum += (7/decimalCounter); // decimalCounter is initialized at 10. So putting in "8.1" means 8 + 1/10 = 8.1
					decimalCounter *= 10;
					
				}
					// there can't be any decimals after an already decimal'd number, e.g. 9.2.5 can't exist
				else{
					resultNum *= 10;
					resultNum += 7;
					decimalFlag = 0;
				}
				break;
			case R.id.b8: 
				results += "8";
				operatorAllowed = 1;
				
				if( decimalFlag == 1 ){ // if there is a decimal in place, we need to do something different with the math
					resultNum += (8/decimalCounter); // decimalCounter is initialized at 10. So putting in "8.1" means 8 + 1/10 = 8.1
					decimalCounter *= 10;
					
				}
					// there can't be any decimals after an already decimal'd number, e.g. 9.2.5 can't exist
				else{
					resultNum *= 10;
					resultNum += 8;
					decimalFlag = 0;
				}
				break;
			case R.id.b9: 
				results += "9";
				operatorAllowed = 1;
				
				if( decimalFlag == 1 ){ // if there is a decimal in place, we need to do something different with the math
					resultNum += (9/decimalCounter); // decimalCounter is initialized at 10. So putting in "8.1" means 8 + 1/10 = 8.1
					decimalCounter *= 10;
					
				}
					// there can't be any decimals after an already decimal'd number, e.g. 9.2.5 can't exist
				else{
					resultNum *= 10;
					resultNum += 9;
					decimalFlag = 0;
				}
				break;
				
				
			// This is the OPERATORS section
			// What happens first is the code needs to know if an operator input is allowed or not. This is to prevent invalid expressions
			// from being entered.
			
			// The string representation showed to the user gets the operator sign tacked on (no-brainer).
			// Then here's the tougher part:
			// The [resultNum] previously incremented from entering the number will be stored, as a STRING, to a string array called data[]
			// The reason for this you will see soon.
				
			// For example's sake, let's just say you just entered 82+ in the calculator
			// [resultNum] would equal 82 in double format
			// The moment you press the "+" button, what happens is 82 becomes "82" (string) and is stored into the data array
			// The array index is incremented and the string "+" is put in
			// So data[] is now ["82"|"+"]
			
			case R.id.bAdd:
				if( operatorAllowed == 1){
					// Display operator sign on display
					results += "+";
					// Put accumulated number, converted into string format, into array space
					if (negFlag == 1)
						resultNum = resultNum*(-1);
					data[index] = Double.toString(resultNum);
					resultNum=0;
					// Increment index
					index++;
					// Put the "+" sign in
					data[index] = "+";
					//Increment again
					index++;
					
					operatorAllowed = 0;
					decimalFlag = 0;
					negFlag = 0;
					specialView.setText("");
				}
				break;
			case R.id.bSub:
				if( operatorAllowed == 1){
					// Display operator sign on display
					results += "-";
					// Put accumulated number, converted into string format, into array space
					if (negFlag == 1)
						resultNum = resultNum*(-1);
					data[index] = Double.toString(resultNum);
					resultNum=0;
					// Increment index
					index++;
					// Put the "+" sign in
					data[index] = "-";
					//Increment again
					index++;
					operatorAllowed = 0;
					decimalFlag = 0;
					negFlag = 0;
					specialView.setText("");
				}
				break;
			// The purpose for the operator flag is because MULTIPLICATION and DIVISION have a higher priority over add/subtract
			// Recall "BEDMAS"
			// So there needs to be a flag variable for that when all the numbers are processed later
			case R.id.bMul: 
				if( operatorAllowed == 1){
					// Display operator sign on display
					results += "x";
					// Put accumulated number, converted into string format, into array space
					if (negFlag == 1)
						resultNum = resultNum*(-1);
					data[index] = Double.toString(resultNum);
					resultNum=0;
					// Increment index
					index++;
					// Put the "x" sign in
					data[index] = "x";
					//Increment again
					index++;			
					operatorFlag = 1;
					operatorAllowed = 0;
					decimalFlag = 0;
					negFlag = 0;
					specialView.setText("");
				}
				break;
			case R.id.bDiv:
				if( operatorAllowed == 1){
					// Display operator sign on display
					results += "/";
					// Put accumulated number, converted into string format, into array space
					if (negFlag == 1)
						resultNum = resultNum*(-1);
					data[index] = Double.toString(resultNum);
					resultNum=0;
					// Increment index
					index++;
					// Put the "+" sign in
					data[index] = "/";
					//Increment again
					index++;
					
					operatorAllowed = 0;
					operatorFlag = 1;
					decimalFlag = 0;
					negFlag = 0;
					specialView.setText("");
				}
				break;
			case R.id.bC: 
				// clear display
				results = "";
				// clear data array
				for( int i = 0; i < index ; i++){ // **********************possible bug here, data.length-1 maybe?
					data[i] = "";
				}
				index = 0;
				for( int j = 0; j < 10; j++){
					resultInter[j] = 0;
					operatorPosition[j] = 0;
				}
				
				operatorAllowed = 0;
				operatorFlag = 0;
				decimalFlag = 0;
				negFlag = 0;
				specialView.setText("");
				break;
			case R.id.bBrackets: 
				// display bracket 
				if (bracketCount == 0){
					results += "(";
					bracketCount = 1;
					
					// Put accumulated number, converted into string format, into array space
					data[index] = Double.toString(resultNum);
					resultNum=0;
					// Increment index
					index++;
					// Put the "(" sign in
					data[index] = "(";
					//Increment again
					index++;
					decimalFlag = 0;
				}
				else{
					results += ")";
					bracketCount = 0;
					
					// Put accumulated number, converted into string format, into array space
					data[index] = Double.toString(resultNum);
					resultNum=0;
					// Increment index
					index++;
					// Put the ")" sign in
					data[index] = ")";
					//Increment again
					index++;
					decimalFlag = 0;
				}
				
				break;
				
			case R.id.bBack:
				results = results.substring(0, results.length()-1);
				// need to delete numbers on screen, but not all numbers in string array
				// so operators would need to be deleted
				if( index > 0 && (data[(index-1)] == "+" || data[(index-1)] == "x" || data[(index-1)] == "-" || data[(index-1)] == "/" )){ 
					// avoid out of bounds
					data[(index-1)]= "";
					index--;
				}
				// but what about if backspacing on a number? we shouldn't need to do anything
				// since numbers only go into the "data array" 
				operatorAllowed = 1;
				decimalFlag = 0;
				break;
			case R.id.bPosNeg:
				// changes signs of the current number. nothing should happen when the most recent entry was a decimal or operator.
				
				// the display part (string)
				
				
				// the math part
				
				if( negFlag == 1){
					negFlag = 0;
					specialView.setText("");
				}
				specialView.setText("(-)");
				negFlag = 1;
				break;
				
			case R.id.bDecimal: 
				if( decimalFlag == 0){
					results = results + ".";
					decimalFlag = 1;
					operatorAllowed = 0;
				}
				break;
							
			case R.id.bExponent:
				if( operatorAllowed == 1){
					// Display operator sign on display
					results += "^";
					// Put accumulated number, converted into string format, into array space
					if (negFlag == 1)
						resultNum = resultNum*(-1);
					data[index] = Double.toString(resultNum);
					resultNum=0;
					// Increment index
					index++;
					// Put the "^" sign in
					data[index] = "^";
					//Increment again
					index++;
					
					operatorAllowed = 0;
					operatorFlag = 2;
					decimalFlag = 0;
					negFlag = 0;
					specialView.setText("");
				}
				break;
			
			case R.id.bEqual:
									
				// Put accumulated number, converted into string format, into array space (IF ANY)
				if (negFlag == 1)
					resultNum = resultNum*(-1);
				data[index] = Double.toString(resultNum);
				
				
				 
				// scan for higher level operators
				// operatorFlag =/= 0 means there is a mul/div/etc
				// else just do normal addition or subtraction
				if ( operatorFlag > 0 ){
					
					int count = 0; // this is kept to patch up the shortcoming of my whole "do a mul/div operation and replace the numbers with 0 and the operator with the result" algorithm
					// after one mul/div operation is done, count goes up so i dont get the error of dividing 0 by the next "resultInter"
					
					// first part of this longer algorithm is to find where the higher level operators are
					// if its a mul, make the position flag positive
					// div? negative.
					int j = 0;
					for( int i=0; i<index+1; i++){
						if( data[i] == "x"){
							operatorPosition[j] = i;
							temp = operatorPosition[j];
							j++;
							
							
							// explanation of confusing code:
							// I'm either multiplying or dividing the numbers ADJACENT to the mul or div operator
														
							// I used an array to store the intermediate products of mul/div because there could be a lot of them
			
							System.out.println(temp-1);
							if(data[temp-1] != "" || data[temp+1] != ""){
								resultInter[j] = Double.valueOf(data[Math.abs(temp-1)-count])*Double.valueOf(data[temp+1]);
							}
							// put result back into the data array...as a string
							data[temp] = Double.toString(resultInter[j]);
							data[Math.abs(temp-1)] = "0";
							data[temp+1] = "0";
							
							if( count > 0){
								// for the test case 8/3/2
								// we need to take out 2.66 in 0 2.66 0 1.33 
								// or else the answer will become 2.66+1.33 instead of just 1.33 (correct)
								data[temp-count-1] = "0";
							}
							count++;
						}
						if( data[i] == "/"){
							operatorPosition[j] = i;
							temp = operatorPosition[j];
							j++;
														
							
							System.out.println("The division about to occur "+data[Math.abs(temp-1)-count]+" / "+data[Math.abs(temp+1)]);
							System.out.println("Array slots: " +(Math.abs(temp-1)-count)+" "+Math.abs(temp+1));
							
							if(data[temp-1] != "" || data[temp+1] != ""){
								resultInter[j] = Double.valueOf(data[Math.abs(temp-1)-count]) / Double.valueOf(data[temp+1]);
							}
							System.out.println("And the result is : "+resultInter[j]+" in resultInter["+j+"]");
							// put result back into the data array...as a string
							data[temp] = Double.toString(resultInter[j]);
							data[Math.abs(temp-1)] = "0";
							data[temp+1] = "0";
							
							if( count > 0){
								// for the test case 8/3/2
								// we need to take out 2.66 in 0 2.66 0 1.33 
								// or else the answer will become 2.66+1.33 instead of just 1.33 (correct)
								data[temp-count-1] = "0";
							}
							count++;
							
						}
						if( data[i] == "^"){
							operatorPosition[j] = i;
							temp = operatorPosition[j];
							j++;
							
							
							if(data[temp-1] != "" || data[temp+1] != ""){
								resultInter[j] = Math.pow(Double.valueOf(data[Math.abs(temp-1)-count]),Double.valueOf(data[temp+1]));
							}
							
							// put result back into the data array...as a string
							data[temp] = Double.toString(resultInter[j]);
							data[Math.abs(temp-1)] = "0";
							data[temp+1] = "0";
							
							if( count > 0){
								// for the test case 8/3/2
								// we need to take out 2.66 in 0 2.66 0 1.33 
								// or else the answer will become 2.66+1.33 instead of just 1.33 (correct)
								data[temp-count-1] = "0";
							}
							count++;
							
						}
					}
					
											
					
					
				
					// third part of algorithm: same as below.
					// our data array probably went from something like:
					// [8|+|2|x|5|+|6]
					// to
					// [8|+| |10| |+|6]
					
					resultTemp = 0;
					for( int i=0; i<(index+1); i++){
						if( data[i] == "+" )
							addFlag = 1;
						else if( data[i] == "-" )
							addFlag = -1;
						else if( data[i] == "" || data[i] == null)
							addFlag = addFlag; // aka. do nothing
						else
							resultTemp = resultTemp + addFlag*(Double.valueOf(data[i]));
						
					}
					
				}
				
				// this is where a normal addition/subtraction is done (yes, the mul/div algorithm is way longer)
				else{
					resultTemp = 0;
					for( int i=0; i<(index+1); i++){
						if( data[i] == "+" )
							addFlag = 1;
						else if( data[i] == "-" )
							addFlag = -1;
						else if( data[i] == "" )
							addFlag = addFlag; // aka. do nothing
						else if( data[i] == "(" || data[i] == ")" )
							resultTemp = resultTemp * Double.valueOf(data[i+1]);
						else
							resultTemp = resultTemp + addFlag*(Double.valueOf(data[i]));
						
					}
				}
			
				
				for(int i=0; i<index+1; i++)
					System.out.println(data[i]);
				
				results = Double.toString(resultTemp); // converts resultTemp back to string to display to user
	
				
				
				
				
				// set everything back to zero or pressing "=" will continually increment (big mistake of forgetting not to reset!)
				resultNum = 0;
				
				operatorFlag = 0;
				operatorAllowed = 0;
				index = 0;
				addFlag = 1; // by default, adding
				decimalFlag = 0;
				decimalCounter = 10;
				negFlag = 0;
				
				
				
				
				
				// probably delete this part when you want to do continuation of arithmetic after pressing "="
				for( int i = 0; i < index ; i++){ // **********************possible bug here, data.length-1 maybe?
					data[i] = "";
				}
				index = 0;
				for( int j = 0; j < 10; j++){
					resultInter[j] = 0;
					operatorPosition[j] = 0;
				}
				
				break;
				
		}
		
		
		//update results
		textResult.setText(results);
	}

}
