package app;

import java.io.*;
import java.util.*;
import java.util.regex.*;

import structures.Stack;

public class Expression {

	public static String delims = " \t*+-/()[]";
			
    /**
     * Populates the vars list with simple variables, and arrays lists with arrays
     * in the expression. For every variable (simple or array), a SINGLE instance is created 
     * and stored, even if it appears more than once in the expression.
     * At this time, values for all variables and all array items are set to
     * zero - they will be loaded from a file in the loadVariableValues method.
     * 
     * @param expr The expression
     * @param vars The variables array list - already created by the caller
     * @param arrays The arrays array list - already created by the caller
     */
    public static void 
    makeVariableLists(String expr, ArrayList<Variable> vars, ArrayList<Array> arrays) {
    	/** COMPLETE THIS METHOD **/
    	/** DO NOT create new vars and arrays - they are already created before being sent in
    	 ** to this method - you just need to fill them in.
    	 **/
    	for(int i=0; i < expr.length(); i++) {
    		if(expr.charAt(i) == '[' && i > 0) {
    			for(int z = i-1; z >= 0; z--) {
    				if(!Character.isLetter(expr.charAt(z))) {
    					int count = z+1;
    	    			Array temp = new Array(expr.substring(count,i));
    	    			arrays.add(temp);
    	    			break;
    				}
    				else if(z==0) {
    					Array temp = new Array(expr.substring(0,i));
    					arrays.add(temp);
    					break;
    				}
    			}
    		}
    		else if(Character.isLetter(expr.charAt(i))) {
    			for(int z = i; z < expr.length(); z++) {
    				if(!Character.isLetter(expr.charAt(z)) ) {
    					if(expr.charAt(z) == '[')
    					break;
    					else {
    					int count = z;
    	    			Variable temp = new Variable(expr.substring(i, count));
    	    			i = count-1;
    	    			vars.add(temp);
    	    			break;
    					}
    				}
    				else if(z == expr.length()-1) {
    					Variable temp = new Variable(expr.substring(i));
    					vars.add(temp);
    					break;
    				}
    			}
    		}
    	}
}
    
    /**
     * Loads values for variables and arrays in the expression
     * 
     * @param sc Scanner for values input
     * @throws IOException If there is a problem with the input 
     * @param vars The variables array list, previously populated by makeVariableLists
     * @param arrays The arrays array list - previously populated by makeVariableLists
     */
    public static void 
    loadVariableValues(Scanner sc, ArrayList<Variable> vars, ArrayList<Array> arrays) 
    throws IOException {
        while (sc.hasNextLine()) {
            StringTokenizer st = new StringTokenizer(sc.nextLine().trim());
            int numTokens = st.countTokens();
            String tok = st.nextToken();
            Variable var = new Variable(tok);
            Array arr = new Array(tok);
            int vari = vars.indexOf(var);
            int arri = arrays.indexOf(arr);
            if (vari == -1 && arri == -1) {
            	continue;
            }
            int num = Integer.parseInt(st.nextToken());
            if (numTokens == 2) { // scalar symbol
                vars.get(vari).value = num;
            } else { // array symbol
            	arr = arrays.get(arri);
            	arr.values = new int[num];
                // following are (index,val) pairs
                while (st.hasMoreTokens()) {
                    tok = st.nextToken();
                    StringTokenizer stt = new StringTokenizer(tok," (,)");
                    int index = Integer.parseInt(stt.nextToken());
                    int val = Integer.parseInt(stt.nextToken());
                    arr.values[index] = val;              
                }
            }
        }
    }
    
    /**
     * Evaluates the expression.
     * 
     * @param vars The variables array list, with values for all variables in the expression
     * @param arrays The arrays array list, with values for all array items
     * @return Result of evaluation
     */
    public static float 
    evaluate(String expr, ArrayList<Variable> vars, ArrayList<Array> arrays) {
    	/** COMPLETE THIS METHOD **/
    	// following line just a placeholder for compilation
    	boolean check = true;
    	for(int b = 0; b < expr.length(); b++) {
    		if(b == 0) {
    			if(Character.isLetter(expr.charAt(b))) {
    				check = false;
    			}
    			else continue;
    		}
    		else if(!Character.isDigit(expr.charAt(b)) && expr.charAt(b)!=' ' && expr.charAt(b)!='.') {
    			check = false;
    			break;
    		}
    	}
    	if(check) {
    		return Float.parseFloat(expr);
    	}
    	for(int i = 0; i < expr.length();i++) {
    		if(expr.charAt(i) == '(') {
    			for(int k = i; k<expr.length(); k++) {
    				if(expr.charAt(k) == ')') {
    					int count = 0;
    					for(int z = k-1; z > i; z--) {
    						if(expr.charAt(z) == ')')
    							count +=1;
    						else if(expr.charAt(z) == '(')
    							count -=1;
    					}
    					if(count == 0) {
    						String temp = expr.substring(i+1,k);
    						expr = expr.substring(0,i) + Float.toString(evaluate(temp,vars,arrays)) + expr.substring(k+1);
    						i = 0;
    					}
    				}
    			}
    		}
    	}
    	for(int z = 0; z < expr.length(); z++) {
    		if(expr.charAt(z) == '[') {
    			for(int i = z+1; i < expr.length(); i++) {
    				if(expr.charAt(i) == ']') {
    					int count1 = 0, count2 = 0;
    					for(int a = z; a <= i;a++) {
    						if(expr.charAt(a) == '[') {
    							count1 +=1;
    						}
    						else if(expr.charAt(a) == ']') {
    							count2 +=1;
    						}
    					}
    					if(count1 == count2) {
    						for(int k = z-1; k >=0; k--) {
    							if(!Character.isLetter(expr.charAt(k)) && expr.charAt(k)!='.') {
    								count1 = k+1;
    								Array temp = new Array(expr.substring(count1,z));
    								count2 = arrays.indexOf(temp);
    								break;
    							}
    							else if(k == 0) {
    								count1 = k;
    								Array temp = new Array(expr.substring(k,z));
    								count2 = arrays.indexOf(temp);
    							}
    						}
						expr = expr.substring(0, count1) + Integer.toString(arrays.get(count2).values[(int)(evaluate(expr.substring(z+1, i), vars, arrays))]) + expr.substring(i+1);
						z = 0;
						break;
    					}
    				}
    			}
    		}
    		else if(Character.isLetter(expr.charAt(z))) {
    			for(int c = z; c < expr.length();c++) {
    				if(!Character.isLetter(expr.charAt(c)) && expr.charAt(c)!= '[' && expr.charAt(c) != '.') {
    					Variable temp = new Variable(expr.substring(z,c));
    					int count = vars.indexOf(temp);
    	    			expr = expr.substring(0, z) + Integer.toString(vars.get(count).value) + expr.substring(c);
    	    			z = 0;
    	    			break;
    				}
    				else if(!Character.isLetter(expr.charAt(c)) && expr.charAt(c) != '.') {
    					break;
    				}
    				else if(c==expr.length()-1) {
    					Variable temp = new Variable(expr.substring(z,c+1));
    					int count = vars.indexOf(temp);
    					expr = expr.substring(0,z) + Integer.toString(vars.get(count).value) + expr.substring(c+1);
    					z=0;
    					break;
    				}
    			}
    		}
    	}
    	float temp = 0;
    	int count1=0, count2= 0;
    	for(int m = 0; m < expr.length(); m++) {
    		if(expr.charAt(m) == '*' && Character.isDigit(expr.charAt(m-1)) && Character.isDigit(expr.charAt(m+1))) {
    			for(int o = m+1; o < expr.length(); o++) {
    				if(!Character.isDigit(expr.charAt(o)) && expr.charAt(o) != '.') {
    					count2 = o-1;
    					break;
    				}
    				else if(o == expr.length()-1) {
    					count2 = o;
    					break;
    				}
    			}
    			for(int c = m-1; c >= 0; c--) {
    				if(!Character.isDigit(expr.charAt(c)) && expr.charAt(c) != '.') {
    					count1 = c+1;
    					break;
    				}
    				else if(c == 0) {
    					count1 = c;
    					break;
    				}
    			}
    			temp = Float.parseFloat(expr.substring(count1, m)) * Float.parseFloat(expr.substring(m+1, count2+1));
    			expr = expr.substring(0, count1) + Float.toString(temp)+ expr.substring(count2+1);
    		}
    	}
    	for(int d = 0; d < expr.length(); d++){
    		if(expr.charAt(d) == '/' && Character.isDigit(expr.charAt(d-1)) && Character.isDigit(expr.charAt(d+1))) {
    		for(int q = d+1; q < expr.length(); q++) {
    			if(!Character.isDigit(expr.charAt(q)) && expr.charAt(q)!='.') {
    				count2 = q-1;
    				break;
    			}
    			else if(q == expr.length()-1) {
    				count2 = q;
    				break;
    			}
    		}
    		for(int c = d-1; c >= 0; c--) {
    			if(!Character.isDigit(expr.charAt(c))&&expr.charAt(c)!='.') {
    				count1 = c+1;
    				break;
    			}
    			else if(c == 0) {
    				count1 = c;
    				break;
    			}
    		}
    		temp = Float.parseFloat(expr.substring(count1, d)) / Float.parseFloat(expr.substring(d+1, count2+1));
			expr = expr.substring(0, count1) + expr.substring(count2+1);
    		}
    	}
    	for(int p = 1; p < expr.length(); p++) {
    		if(expr.charAt(p) == '+' && Character.isDigit(expr.charAt(p-1)) && Character.isDigit(expr.charAt(p+1))) {
    			temp = evaluate(expr.substring(0, p), vars, arrays) + evaluate(expr.substring(p+1), vars, arrays);
    			expr = Float.toString(temp);
    		}
    	}
    	for(int mi = 1; mi < expr.length(); mi++) {
    		if(expr.charAt(mi) == '-' && Character.isDigit(expr.charAt(mi-1)) && Character.isDigit(expr.charAt(mi+1))) {
    			temp = evaluate(expr.substring(0, mi), vars, arrays) - evaluate(expr.substring(mi+1), vars, arrays);
    			expr = Float.toString(temp);
    		}
    	}
    	return evaluate(expr,vars,arrays);
    }
}