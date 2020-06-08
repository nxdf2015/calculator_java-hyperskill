package calculator;

import java.math.BigInteger;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;
import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Main{

    public static void main(String[] args) throws Exception {
        Scanner scanner = new Scanner(System.in);
        Calculator calculator = new Calculator();


     String line ;
     while(true){
         String result ="";
         line = scanner.nextLine();
         if (line.isEmpty()){
             continue;
         }
         else if (line.startsWith("/")){

              if (line.equals("/exit")){
                 break;
             }
             else if (line.equals("/help")){
                 result = "The program calculates the sum of numbers";
             }
             else {
                 result = "Unknown command";
             }
         }
         else  {
            try {
                if (calculator.isVariable(line)) {
                    calculator.setVariable(line);
                    continue;
                }
                else {

                    //result = calculator.compute(line);
                    result = calculator.computeExpression(line);
                }
            }
            catch (InvalidExpression e){
                result = "Invalid expression";
            }
            catch (UnknownVariable e){
                result = "Unknown variable";
            }
            catch (InvalidAssignment e){
                result ="Invalid assignment";
            }
            catch (InvalidIdentifier e ){
                result = "Invalid identifier";
            }


         }

         System.out.println(result);
     }
        System.out.println("Bye!");

    }
}
