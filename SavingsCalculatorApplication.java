package application;

import javafx.application.Application;
import javafx.stage.Stage;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.scene.chart.LineChart;
import javafx.scene.layout.VBox;
import javafx.beans.value.ObservableValue;
import javafx.scene.control.Slider;
import javafx.scene.control.Label;
import java.util.ArrayList;
import javafx.scene.chart.XYChart;
import javafx.scene.chart.NumberAxis;

public class SavingsCalculatorApplication extends Application {
    
    NumberAxis YAxis = new NumberAxis(0, 100000, 5000);
    
    public static void main(String[] args) {
        launch(args);
    }
    
    @Override
    public void start(Stage window) {
        BorderPane borderPane = new BorderPane();
        BorderPane v1 = new BorderPane();
        BorderPane v2 = new BorderPane();
        VBox vbox = new VBox();
        Slider slider1 = new Slider(25, 250, 25);
        Slider slider2 = new Slider(0, 10, 0);
        slider1.setShowTickLabels(true);
        slider1.setShowTickMarks(true);
        slider2.setShowTickLabels(true);
        slider2.setShowTickMarks(true);
        
        v1.setLeft(new Label("Monthly savings"));
        v1.setCenter(slider1);
        Label first = new Label(String.format("%.2f", slider1.getValue()));
        Label second = new Label(String.format("%.2f", slider2.getValue()));
        v1.setRight(first);
        v2.setLeft(new Label("Yearly interest rate"));
        v2.setCenter(slider2);
        v2.setRight(second);
        
        Double s1Val = Double.valueOf(first.getText());
        Double s2Val = Double.valueOf(second.getText());
            
        XYChart.Series<Number, Number> series = new XYChart.Series<>();
        series.setName(String.valueOf(s1Val));

        XYChart.Series<Number, Number> interest = new XYChart.Series<>();
        interest.setName(String.valueOf(s2Val));

        slider1.valueProperty().addListener(
            (ObservableValue<? extends Number> observable, Number oldValue, Number newValue) -> {
                first.setText(String.format("%.2f", newValue));
                updateChart(slider1, slider2, series, interest);
            }
        );
        slider2.valueProperty().addListener(
            (ObservableValue<? extends Number> observable, Number oldValue, Number newValue) -> {
                second.setText(String.format("%.2f", newValue));
                updateChart(slider1, slider2, series, interest);
            }
        );  
        
        vbox.getChildren().addAll(v1, v2);
        
        NumberAxis XAxis = new NumberAxis(0, 30, 1);
        LineChart<Number, Number> lineChart = new LineChart(XAxis, YAxis);
        lineChart.getData().add(series);
        lineChart.getData().add(interest);
        borderPane.setCenter(lineChart);
        borderPane.setTop(vbox);

        Scene view = new Scene(borderPane);
        window.setScene(view);
        window.show();
        
        updateChart(slider1, slider2, series, interest);
    }

    private void updateChart(Slider slider1, Slider slider2, XYChart.Series<Number, Number> series, XYChart.Series<Number, Number> interest) {
        series.getData().clear(); 
        interest.getData().clear();

        double monthlySavings = slider1.getValue();
        double annualInterestRate = slider2.getValue();
        int years = 30;
        
        ArrayList<Double> values = new ArrayList<>();       
        ArrayList<Double> interestValues = new ArrayList<>();
        
        double total = 0;
        for (int i = 0; i < years; i++) {
            total+= (monthlySavings * 12);
            values.add(total);
        }
        
        double totalSavings = 0.0;
        for (int i = 0; i < years; i++) {
            totalSavings = (totalSavings + (monthlySavings * 12));
            double interestValue = (totalSavings / 100) * annualInterestRate;
            totalSavings += interestValue;
            interestValues.add(totalSavings);
        }

        double maxInterestValue = interestValues.stream().max(Double::compare).orElse(0.0);
        YAxis.setUpperBound(maxInterestValue + 5000);
        
        series.setName(String.format("Monthly Savings: %.2f", monthlySavings));
        series.getData().add(new XYChart.Data<>(0, 0));
        for (int i = 0; i < values.size(); i++) {
            series.getData().add(new XYChart.Data<>(i + 1, values.get(i)));
        }
        
        interest.setName(String.format("Savings including interest of: %.2f", annualInterestRate));
        interest.getData().add(new XYChart.Data<>(0,0));
        for (int i = 0; i < interestValues.size(); i++) {
            interest.getData().add(new XYChart.Data<>(i + 1, interestValues.get(i)));
        }
    }
}
