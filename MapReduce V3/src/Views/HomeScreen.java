package Views;

import System.General.CheckUtils;
import System.IO.IOReport;
import System.IO.IOWrite;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.TextAlignment;
import System.MapReduceSystem;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

public class HomeScreen implements Screen {

    private Label maperFunLbl,reducerFunLbl,numOfMaperLbl,numOfReducerLbl,status;
    private TextField numOfMappers,numOfReducers;
    private TextArea reducerFunction,mapperFunction;
    private Button startBtn;
    private Scene scene;

    public HomeScreen(){

        maperFunLbl = new Label("Mapper function");
        reducerFunLbl = new Label("Reducer function");
        startBtn = new Button("Start");
        mapperFunction = new TextArea("");
        reducerFunction = new TextArea("");
        numOfMaperLbl = new Label("Number of mappers");
        numOfReducerLbl = new Label("Number of mappers");
        numOfMappers = new TextField("2");
        numOfReducers = new TextField("2");

        mapperFunction.setDisable(true);
        reducerFunction.setDisable(true);

        VBox MapperAll = new VBox(maperFunLbl,mapperFunction);
        VBox ReducerAll = new VBox(reducerFunLbl,reducerFunction);
        HBox functions = new HBox(MapperAll,ReducerAll);

        mapperFunction.setPrefWidth(1000);
        reducerFunction.setPrefWidth(1000);
        mapperFunction.setPrefHeight(1000);
        reducerFunction.setPrefHeight(1000);

        numOfReducers.setPrefWidth(1000);
        numOfMappers.setPrefWidth(1000);
        functions.setSpacing(10);

        VBox numMaperAll = new VBox(numOfMaperLbl,numOfMappers);
        VBox numReducerAll = new VBox(numOfReducerLbl,numOfReducers);

        numMaperAll.setPadding(new Insets(10));
        numReducerAll.setPadding(new Insets(10));

        HBox nums = new HBox(numMaperAll,numReducerAll);
        startBtn.setMaxWidth(200);
        startBtn.setMinHeight(50);
        startBtn.setPadding(new Insets(15));
        startBtn.setBorder(Border.EMPTY);

        status = new Label("IDEL");
        status.setPadding(new Insets(10));
        status.setTextFill(Color.GREEN);
        status.setFont(Font.font(18));
        //status.setPrefWidth(200);
        status.setTextAlignment(TextAlignment.RIGHT);
        //HBox statusStart = new HBox(startBtn,status);
        VBox root = new VBox(functions, nums,startBtn,status);


        root.setPadding(new Insets(10));

        scene = new Scene(root,1200,700);

        startBtn.setOnAction(event -> startBtnFunction());

    }

    public Scene getScreen(){
        return scene;
    }

    private void startBtnFunction(){
        status.setText("Working");
        status.setTextFill(Color.BLUE);
        if(
                numOfReducers.getText() == null || !CheckUtils.isNumric(numOfReducers.getText()) ||
                numOfMappers.getText() == null || !CheckUtils.isNumric(numOfMappers.getText())
        ){
            status.setText("Error , Enter Correct number of reducers and mappers");
            status.setTextFill(Color.RED);
        }
        else{
            IOReport report = new IOReport("Logs");
            report.addMessage("Start Time : " + CheckUtils.getTimeNow());
            report.addMessage("Number of Mappers : " + numOfMappers.getText());
            report.addMessage("Number of Reducers : " + numOfReducers.getText());
            long startTime = System.currentTimeMillis();
            long startMemory = Runtime.getRuntime().totalMemory()-Runtime.getRuntime().freeMemory();
            //----------------------------------------------------------------------------------------------------
            int reducers = Integer.parseInt(numOfReducers.getText());
            int mapers = Integer.parseInt(numOfMappers.getText());
            startBtn.setDisable(true);
            MapReduceSystem system = new MapReduceSystem(mapers,reducers);
            try{
                system.setInput("input.txt");
                system.setOutput("output.txt");
                system.start();
            }
            catch (IOException ex){
                System.out.println("Error in input or output files");
            }
            //----------------------------------------------------------------------------------------------------

            long finishTime = System.currentTimeMillis();
            long finishMemory = Runtime.getRuntime().totalMemory()-Runtime.getRuntime().freeMemory();

            double takenTime = (finishTime-startTime+0.0)/1000;
            double takenMemory = (finishMemory-startMemory+0.0)/(1024*1024);
            status.setText("Operation Done Successfully");
            report.addMessage("Finish Time : " + CheckUtils.getTimeNow());
            report.addMessage("Taken time is : " + takenTime + "s");
            report.addMessage("Taken Memory is : " + takenMemory + "MB");
            report.flush();
            startBtn.setDisable(false);
        }

    }

}
