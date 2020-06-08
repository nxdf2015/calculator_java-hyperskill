package calculator;

import java.util.*;
import java.util.function.BiFunction;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Calculator {
    public Map<String,Integer> variables;

    public Calculator() {
        variables =new HashMap<>();
    }

    public String compute(String   data) throws Exception {

        List<Integer> numbers = parseData(data);
            return  numbers.stream()
                       .reduce(0,(acc,x) -> acc + x)
                      .toString();



    }

    public void setVariable(String line) throws InvalidAssignment, UnknownVariable, InvalidIdentifier {
        Pattern variablePattern = Pattern.compile("([a-z]+\\s*=\\s*[^=]+)");

        String[] items = line.split("\\s*=\\s*");

        if (!Pattern.matches("[a-zA-Z]+",items[0])){

            throw new InvalidIdentifier();
        }

        Matcher matcher = variablePattern.matcher(line);
        matcher.find();
        String group = matcher.group(1);
//        if (isVariable(line))
        {

            if (matcher.end() != line.strip().length()){
                throw new InvalidAssignment();
            }
            parseVariable(group);

        }
    }

    public List<Integer>  parseData(String data) throws Exception {
        Pattern token = Pattern.compile("([+-]*\\s*([a-z]+|\\d+))",Pattern.CASE_INSENSITIVE);

        Matcher matcher = token.matcher(data);

        List<Integer> numbers = new ArrayList<>();
        int last = 0;
        int start = 0;
        while (matcher.find()){
             String group = matcher.group(1);

            last= matcher.end();
            start=matcher.start();

             Pattern digitPattern = Pattern.compile("\\d+");
             Pattern variablePattern = Pattern.compile("[a-z]+",Pattern.CASE_INSENSITIVE);
             Pattern signPattern = Pattern.compile("[+-]");

             Matcher digitMatcher = digitPattern.matcher(group);
             Matcher variableMatcher  = variablePattern.matcher(group);

             digitMatcher.find();
             String digit;
             int i ;

             if(variableMatcher.find()){

                 i = variableMatcher.start();
                 String  var = group.substring(i,group.length());
                 if (variables.containsKey(var)){
                     digit = "" + variables.get(group.substring(i, group.length()) );
                 }
                 else {

                     throw new UnknownVariable();
                 }
             }
             else {
                 i = digitMatcher.start();
                 digit = group.substring(i, group.length());
             }

             String sign ="";

             if (i >= 1 || start == 0)
             {
                  sign = group.substring(0, i).strip();

                  if (start != 0 && !signPattern.matcher(sign).find()){
                      throw new InvalidExpression();
                  }
             }

             else {

                 throw new InvalidExpression();
             }
             numbers.add(getNumber(sign,digit));

        }
        if (last < data.length()){

            System.out.println("Invalid expression");
        }
        return numbers;
    }

    private void parseVariable(String group) throws InvalidAssignment, UnknownVariable {
        String[] items = group.split("\\s*=\\s*");

        if(Pattern.matches("\\w+",items[0])){
            int value;

            if (Pattern.matches("\\d+",items[1])){
                 value = Integer.parseInt(items[1]);
            }
            else if (Pattern.matches("[a-z]+",items[1])) {
                if (variables.containsKey(items[1])){
                    value = variables.get(items[1]);
                }
                else {
                    throw new UnknownVariable();
                }

            }
            else {
                throw new  InvalidAssignment();
            }
            variables.put(items[0],value);
        }
        else {
            throw new InvalidAssignment();
        }
    }

    public List<String> toPostfix(String expression) throws InvalidExpression {

        Deque<Operator> stack = new LinkedList<>();

        Pattern tokenPattern = Pattern.compile("(\\d+|[a-zA-Z]+|[+-/*]+|\\(|\\))");
        String operators = "*-+/";
        Matcher tokenMatcher = tokenPattern.matcher(expression);
        List<String> result = new ArrayList<>();

        while(tokenMatcher.find()){
            String token = tokenMatcher.group(1);

            if(Pattern.matches("(\\d+|[a-zA-Z]+)",token)){
                result.add(token);
                continue;
            }
            Operator current = Operator.create(token);


            if(stack.isEmpty()){
                stack.addLast(current);
                continue;
            }


            Operator last = stack.peekLast();

            if(last.isLeftParenthesis() && !current.isRightParenthesis()){
                stack.addLast(current);
            }
            else if (current.isParenthesis()){

                if (current.isLeftParenthesis()){
                    stack.addLast(current);
                }
                else if (current.isRightParenthesis()){

                    while(!stack.isEmpty() && !stack.peekLast().isLeftParenthesis()){
                        result.add(stack.pollLast().value());
                    }

                    if(stack.isEmpty() || !stack.peekLast().isLeftParenthesis()){
                        throw new InvalidExpression();
                    }
                    stack.pollLast();
                }

            }
            else if (current.isOperator()) {
                if(current.isHighterPrecedenceThan(last)){
                   stack.addLast(current);
                }
                else {
                 while(!stack.isEmpty() && !current.isHighterPrecedenceThan(last)){
                        result.add( stack.pollLast().value());
                        last = stack.peekLast();
                 }

                 stack.addLast(current);
                }
            }

        }

        while(!stack.isEmpty() && stack.peekLast().isOperator()){
            result.add(stack.pollLast().value());
        }

        if (!stack.isEmpty()){
            throw new InvalidExpression();
        }
     return result;
    }

    public int computePostfix(List<String> expression) throws InvalidExpression, UnknownVariable {
        Deque<Integer> stack = new LinkedList<>();
        int i = 0;
        while(i < expression.size()) {
            String item = expression.get(i);
            if (Pattern.matches("(\\d+)", item)) {
                stack.addLast(Integer.parseInt(item));

            } else if (Pattern.matches("([a-zA-Z]+)", item)) {
                if (variables.containsKey(item)) {
                    stack.addLast(variables.get(item));
                }
                else {
                    throw new UnknownVariable();
                }

            }
            else if(stack.size() < 2) {
                throw new InvalidExpression();
            }
            else {
                    int first = stack.pollLast();
                    int second = stack.pollLast();
                    BiFunction<Integer, Integer, Integer> f = null;
                    switch (item) {
                        case "+":
                            f = (x, y) -> x + y;
                            break;
                        case "-":
                            f = (x, y) -> y - x;
                            break;
                        case "*":
                            f = (x, y) -> x * y;
                            break;
                        case "/":
                            f = (x, y) -> y / x;
                            break;
                    }
                    stack.addLast(f.apply(first, second));


            }
            i++;
        }
        if (stack.size()> 1){
            throw new InvalidExpression();
        }
        return stack.pollLast();

    }

    public  boolean isVariable(String group) {
        return group.indexOf("=") != -1;
    }

    public String computeExpression(String expression) throws InvalidExpression, UnknownVariable {
        List<String> postFixExpression = toPostfix(expression);
        return ""+computePostfix(postFixExpression);

    }
    private int getNumber(String signToken, String digitToken){
        char signReduce = reduce(signToken);
        int n = Integer.parseInt(digitToken);
        if (signReduce == '+'){
            return n;
        }
        else {
            return -1 * n;
        }
    }

    private char reduce(String str){
        int countMinus = countSign(str);
        if(countMinus % 2 == 0){
            return '+';
        }
        else {
            return '-';
        }
    }

    private int countSign(String str){
        int count = 0;
        for(int i = 0 ; i < str.length() ; i++ ){
            if (str.charAt(0) == '-'){
                count++;
            }
        }
        return count ;
    }
}
