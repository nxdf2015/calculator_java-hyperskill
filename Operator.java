package calculator;

import java.util.HashMap;
import java.util.Map;

public class Operator implements  Comparable<Operator>{
    String label;
    int precedence;
    public static Operator create(String token) throws InvalidExpression {

        String rt = reduceToken(token);
        switch(rt){
            case "*":
                case "/":
                return new Operator(rt, 2);
            case "+":
            case"-":
                return new Operator(rt ,3);
            case "(":
            case ")":
                return new Operator(rt,4);
            default:
                return null;
        }
    }

    public static String reduceToken(String token) throws InvalidExpression {
        int countMultDiv = 0;
        int countSubs = 0;
        int countAdd = 0 ;
        if (token.equals("(") || token.equals(")")){
            return  token;
        }

        token = token.replaceAll(" ","");

        for(int i = 0 ; i < token.length() ; i++ ){
          char s = token.charAt(i);
          if (s == '*' || s == '/'){
              countMultDiv++;
          }
          else if(s == '-'){
              countSubs++;
          }
          else if(s == '+'){

              countAdd++;
          }
          else {
              throw new InvalidExpression();
          }
        }

        if(countMultDiv > 1 || (countMultDiv == 1 && (countAdd > 0 || countSubs > 0 )))
            throw new InvalidExpression();

     if (countMultDiv == 1){
         return token;
     }
     else {
         return countSubs % 2 == 0 ? "+" : "-";
     }
    }
    private Operator(String label, int precedence) {
        this.label = label;
        this.precedence = precedence;
    }

    public boolean isMultiplication(){
        return label.equals("*");
    }

    public boolean isDivision(){
        return label.equals("/");
    }

    public boolean isAddition(){
        return label.equals("+");
    }

    public boolean isSubstraction(){
        return label.equals("-");
    }

    public boolean isHighterPrecedenceThan(Operator operator){
        return this.compareTo(operator) ==  1;
    }

    @Override
    public int compareTo(Operator operator) {
        return precedence < operator.precedence ? 1 : -1;
    }

    public boolean isLeftParenthesis() {
        return label.equals("(");
    }

    public String value() {
        return label;
    }

    public boolean isRightParenthesis() {
        return label.equals(")");
    }

    @Override
    public String toString() {
        return "Operator{" +
                "label='" + label + '\'' +
                ", precedence=" + precedence +
                '}';
    }

    public boolean isParenthesis() {
        return isLeftParenthesis() || isRightParenthesis();
    }

    public boolean isOperator() {
        return isAddition() || isMultiplication() ||isDivision() ||isSubstraction();
    }
}
