package com.github.bingoohuang.mvel;

import com.github.bingoohuang.pdf.Util;
import lombok.val;
import org.junit.Test;
import org.mvel2.MVEL;
import org.mvel2.ParserContext;
import org.mvel2.compiler.ExecutableStatement;
import org.mvel2.integration.impl.MapVariableResolverFactory;
import org.mvel2.templates.TemplateCompiler;
import org.mvel2.templates.TemplateRuntime;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.google.common.truth.Truth.assertThat;

public class MVELTest {
    @Test
    public void interpret() {
        Map<String, Object> vars = new HashMap<>();
        vars.put("foobar", 100);
        val result = (Boolean) MVEL.eval("foobar > 99", vars);
        if (result) {
            System.out.println("It works!");
        }
    }

    @Test
    public void activityId() {
        Map<String, Object> vars = new HashMap<>();
        vars.put("activityId", "3872149");
        val result = (Boolean) MVEL.eval("activityId == '387249'", vars);
        System.out.println(result);

        vars.put("a", "1");
        vars.put("b", "2");
        vars.put("c", "3");
        vars.put("d", "4");
        // (typename) value
        // Here typename is the name of the primitive data type to which you’re converting, such as short, int, or float.
        System.out.println(MVEL.eval("((float)a+(float)b+(float)c+(float)d)/4", vars));
    }

    @Test
    public void compile() {
        val compiled = MVEL.compileExpression("foobar > 99");
        Map<String, Object> vars = new HashMap<>();
        vars.put("foobar", 100);
        val result = (Boolean) MVEL.executeExpression(compiled, vars);
        if (result) {
            System.out.println("It works!");
        }
    }

    @Test
    public void processBuilder() {
        String exp = "foobar > 99";
        Map<String, Object> vars = new HashMap<>();
        vars.put("foobar", 100);
        val resolver = new MapVariableResolverFactory(vars);
        val script = (ExecutableStatement) MVEL.compileExpression(exp);
        val result = (Boolean) script.getValue(null, resolver);
        if (result) {
            System.out.println("It works!");
        }
    }

    @Test
    public void parse() {
        val pctx = ParserContext.create();
        MVEL.compileExpression("foobar > 99", pctx);
        System.out.println(pctx.getInputs()); // {foobar=class java.lang.Object}
    }

    @Test
    public void usageOfPropertyExpression() {
        Map<String, Object> input = new HashMap<>();
        input.put("employee", new Employee("john", "michale"));
        String lastName = MVEL.evalToString("employee.lastName", input);
        assertThat(lastName).isEqualTo("michale");

        Boolean yes = MVEL.evalToBoolean("employee.lastName == \"john\"", input);
        assertThat(yes).isFalse();

        input.put("numeric", -0.253405);
        Boolean no = MVEL.evalToBoolean("numeric > 0", input);
        assertThat(no).isFalse();
    }

    @Test
    public void testMVELController() {
        Map<String, Object> input = new HashMap<>();
        input.put("employee", new Employee("john", "michale"));

        String lastName = MVEL.evalToString("employee.lastName", input);
        assertThat(lastName).isEqualTo("michale");

        assertThat(MVEL.evalToBoolean("employee.lastName == \"john\"", input)).isFalse();

        List<Employee> employees = new ArrayList<>();
        employees.add(new Employee("john", "michale"));
        employees.add(new Employee("merlin", "michale"));
        input.put("employees", employees);
        @SuppressWarnings("unchecked")
        val eval = (List<Employee>) MVEL.eval("($ in employees if $.firstName == \"john\")", input);
        assertThat(eval).containsExactly(new Employee("john", "michale"));

        assertThat(MVEL.evalToString(Util.loadClasspathResAsString("concat.mvel"), input))
                .isEqualTo("[johnmichale, merlinmichale]");
    }

    @Test
    public void MVELTemplateController() {
        // Usecase1: Injecting the dynamic property to the static HTML content.
        // MVEL supports the decision making tags to place the default values in case of the actual property value is null
        // Input map should contain the key name otherwise it will throw the exception
        String message = "<html>Hello @if{userName!=null && userName!=''}@{userName}@else{}Guest@end{}! Welcome to MVEL tutorial<html>";
        System.out.println("Input Expression:" + message);
        Map<String, Object> inputMap = new HashMap<>();
        inputMap.put("userName", "Blog Visitor");
        System.out.println("InputMap:" + inputMap);
        String compliedMessage = applyTemplate(message, inputMap);
        System.out.println("compliedMessage:" + compliedMessage);

        // Usecase4: Forming dynamic query by binding the dynamic values
        // We can build complex queries by using the decision making tags@if,@else and for loop tags @for
        // We can bind the values from the bean to expression
        String queryExpression = "select * from @{schemaName}.@{tableName} where @{condition}";
        Map<String, Object> queryInput = new HashMap<>();
        queryInput.put("schemaName", "testDB");
        queryInput.put("tableName", "employee");
        queryInput.put("condition", "age > 25 && age < 30");
        String query = applyTemplate(queryExpression, queryInput);
        System.out.println("Dynamic Query:" + query);

        // Usecase5: Forming dynamic API calls
        String weatherAPI = "http://api.openweathermap.org/data/2.5/weather?lat=@{latitude}&lon=@{longitude}";
        Map<String, Object> apiInput = new HashMap<>();
        apiInput.put("latitude", "35");
        apiInput.put("longitude", "139");
        String weatherAPICall = applyTemplate(weatherAPI, apiInput);
        System.out.println("weatherAPICall:" + weatherAPICall);


        val s = MVEL.evalToString("'lat=' + latitude + ' ' + '^lon=' + longitude", apiInput);
        System.out.println(s);
    }

    /**
     * Method used to bind the values to the MVEL syntax and return the complete expression to understand by any other engine.
     */
    public static String applyTemplate(String expression, Map<String, Object> parameterMap) {
        val compliedTemplate = TemplateCompiler.compileTemplate(expression);
        return (String) TemplateRuntime.execute(compliedTemplate, parameterMap);
    }
}
