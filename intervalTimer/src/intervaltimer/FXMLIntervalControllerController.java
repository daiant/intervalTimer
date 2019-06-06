/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package intervaltimer;

import accesoBD.AccesoBD;
import java.io.IOException;
import java.net.URL;
import java.time.Duration;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.Month;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.application.Platform;

import javafx.beans.binding.Bindings;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.*;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import modelo.Grupo;
import modelo.Gym;
import modelo.Sesion;
import modelo.SesionTipo;

/**
 *
 * @author daiant
 */
public class FXMLIntervalControllerController implements Initializable {

    
    private ObservableList<Grupo> listGroups;
    private ObservableList<SesionTipo> listSessionTypes;
    @FXML
    private ListView<Grupo> ListViewGroups;
    @FXML
    private Button btnGraphics;
    @FXML
    private Button btnEdit;
    @FXML
    private Button btnDelete;
    @FXML
    private Button btnWorkout;
    @Override
    public void initialize(URL url, ResourceBundle rb) {
            

        Gym gym = AccesoBD.getInstance().getGym();

        
        listGroups = FXCollections.observableList(gym.getGrupos());
        listSessionTypes = FXCollections.observableList(gym.getTiposSesion());
    // --------------------------- //
    // TEST //
        try {
            ArrayList<Sesion> aux = new ArrayList<>();
            Grupo hola = listGroups.get(0);
            for (int i = 0; i < 10; i++) {
            Instant first = Instant.MIN;
            Sesion s1 = new Sesion();
            s1.setTipo(listSessionTypes.get(0));
            Instant second = Instant.now();
            s1.setDuracion(Duration.between(first, second));
            s1.setFecha(LocalDateTime.of(i, i, i, i, i));
            
            aux.add(s1);
            }
            hola.setSesiones(aux);
        } catch (Exception err )  {
            System.out.println("jeje");
        }
        
        ListViewGroups.setCellFactory(param -> new ListCell<Grupo>() {
            @Override
            protected void updateItem(Grupo item, boolean empty) {
                super.updateItem(item, empty);

                if (empty || item == null || item.getCodigo() == null) {
                    setText(null);
                } else {
                    setText(item.getCodigo());
                }
            }
        });
        try {
        ListViewGroups.setItems(listGroups);
        } catch(NullPointerException e )  {
            System.out.println("No pasa nada.");
        }


    btnGraphics.disableProperty().bind(Bindings.isEmpty(ListViewGroups.getSelectionModel().getSelectedItems()));
    btnEdit.disableProperty().bind(Bindings.isEmpty(ListViewGroups.getSelectionModel().getSelectedItems()));
    btnDelete.disableProperty().bind(Bindings.isEmpty(ListViewGroups.getSelectionModel().getSelectedItems()));
    btnWorkout.disableProperty().bind(Bindings.isEmpty(ListViewGroups.getSelectionModel().getSelectedItems()));

    }

    @FXML
    private void newGroup(ActionEvent event) {
        Label name = new Label("Nombre del grupo: ");
        TextField name_text = new TextField();
        Label desc = new Label("Descripción: ");
        TextArea desc_text = new TextArea();

        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.getDialogPane().setContent(
                new VBox(8,
                        new HBox(name, name_text),
                        new HBox(desc, desc_text)
                ));
        dialog.getDialogPane().setHeaderText("Nuevo grupo");
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.CANCEL,ButtonType.OK);
        dialog.setY(300);
        dialog.setTitle("Novo gropo");
        Optional<ButtonType> result = dialog.showAndWait();
        if(result.get() == ButtonType.OK) {
            Grupo queMalEstáProgramadoEstoJsoler = new Grupo();
            queMalEstáProgramadoEstoJsoler.setCodigo(name_text.getText());
            queMalEstáProgramadoEstoJsoler.setDescripcion(desc_text.getText());
            listGroups.add(queMalEstáProgramadoEstoJsoler);
        }
    }

    @FXML
    private void seeGraphics(ActionEvent event) {
        Dialog<ButtonType> dlg = new Dialog<>();
        dlg.getDialogPane().getButtonTypes().addAll(ButtonType.CLOSE);
        CategoryAxis xAxis = new CategoryAxis();
        NumberAxis yAxis = new NumberAxis();
        xAxis.setLabel("Día");
       
        LineChart<String,Number> lineChart = new LineChart<>(xAxis,yAxis);
        XYChart.Series series1 = new XYChart.Series();
        
        XYChart.Series series2 = new XYChart.Series();
        series1.setName("Duración Teórica");
        series2.setName("Duración Real");
        XYChart.Series series3 = new XYChart.Series();
        series3.setName("Descansos");
        Grupo grupo = listGroups.get(0);
        for(Sesion sesion : grupo.getSesiones()) {
             SesionTipo tipo = sesion.getTipo();
             Number realDuration = (Number) sesion.getDuracion().toMinutes();
             DateTimeFormatter customFormatter = DateTimeFormatter.ofPattern("MM/dd/yyyy");
             String hola = sesion.getFecha().format(customFormatter);
             Number teoric_duration = tipo.getT_ejercicio() * tipo.getNum_ejercicios() * tipo.getNum_circuitos() + tipo.getT_calentamiento();
             series2.getData().add(new XYChart.Data(hola.toString(), realDuration));
             series1.getData().add(new XYChart.Data(hola.toString(),teoric_duration));
             Number descanso = tipo.getD_circuito() * tipo.getNum_circuitos() + tipo.getD_ejercicio() * tipo.getNum_ejercicios();
             series3.getData().add(new XYChart.Data(hola.toString(), descanso));
             System.out.println("Real: " + sesion.getDuracion().toMillis() + " Teórica"+ teoric_duration);
        }
        lineChart.getData().add(series1);
        lineChart.getData().add(series2);
        lineChart.getData().add(series3);
        dlg.getDialogPane().setContent(lineChart);
        Optional<ButtonType> res = dlg.showAndWait();
        if(res.get() == ButtonType.CLOSE) {
            System.out.println("Hola lo conseguĺi");
        }
        
    }

    @FXML
    private void editGroup(ActionEvent event) {
        Grupo aux = ListViewGroups.getSelectionModel().getSelectedItem();
                Label name = new Label("Nombre del grupo: ");
        TextField name_text = new TextField(aux.getCodigo());
        Label desc = new Label("Descripción: ");
        TextArea desc_text = new TextArea(aux.getDescripcion());

        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.getDialogPane().setContent(
                new VBox(8,
                        new HBox(name, name_text),
                        new HBox(desc, desc_text)
                ));
        dialog.getDialogPane().setHeaderText("Nuevo grupo");
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.CANCEL,ButtonType.OK);
        dialog.setY(300);
        dialog.setTitle("Novo gropo");
        Optional<ButtonType> result = dialog.showAndWait();
        if(result.get() == ButtonType.OK) {

            Grupo queMalEstáProgramadoEstoJsoler = new Grupo();
            queMalEstáProgramadoEstoJsoler.setCodigo(name_text.getText());
            queMalEstáProgramadoEstoJsoler.setDescripcion(desc_text.getText());
            listGroups.remove(aux);
            listGroups.add(queMalEstáProgramadoEstoJsoler);
        }
    }

    @FXML
    private void deleteGroup(ActionEvent event) {
        Grupo aux = ListViewGroups.getSelectionModel().getSelectedItem();
          Alert alert = new Alert(AlertType.CONFIRMATION);
        alert.setTitle("QUE HACES LOCO");
        alert.setHeaderText("¿Eliminar " + aux.getCodigo()+"?");
        Optional<ButtonType> result = alert.showAndWait();
        if(result.get() == ButtonType.OK) {
           listGroups.remove(aux);
        }
    }

//    @FXML
//    private void beginWorkout(ActionEvent event) {
//        Grupo aux = ListViewGroups.getSelectionModel().getSelectedItem();
//        TextField name_text = new TextField(aux.getCodigo());
//        TableView typeSession = new TableView();
//        typeSession.setItems(listSessionTypes);
//        TableColumn nombre = new TableColumn("Nombre");
//        TableColumn series = new TableColumn("Series");
//        nombre.setCellValueFactory(
//                new PropertyValueFactory<SesionTipo,String>("codigo")
//        );
//        series.setCellValueFactory(new PropertyValueFactory<SesionTipo,String>("num_circuitos"));
//        typeSession.getColumns().addAll(nombre, series);
//        //-------//
//        TextField new_name = new TextField();
//        TextField tiempo_calentamiento = new TextField();
//        TextField num_ejer = new TextField();
//        TextField tiempo_ejer = new TextField();
//        TextField desc_ejer = new TextField();
//        TextField num_series = new TextField();
//        TextField desc_serie = new TextField();
//        Dialog<ButtonType> dialog = new Dialog<>();
//        Button addSession = new Button("añadir");
//        dialog.getDialogPane().setContent(
//                new HBox(10,
//                    new VBox(8,
//                        new HBox(new Label("Grupo: "), name_text),
//                        new HBox(new Label("Sesión: "), typeSession)
//                ),
//                    new VBox(8,
//                        new Label("Añadir sesión"),
//                        new HBox(new Label("Nombre: "), new_name),
//                        new HBox(new Label("Duración calentamiento: "), tiempo_calentamiento),
//                        new HBox(new Label("Número ejercicios: "), num_ejer),
//                        new HBox(new Label("Duración ejercicios: "), tiempo_ejer),
//                        new HBox(new Label("Descanso entre ejercicios: "), desc_ejer),
//                        new HBox(new Label("Número de series: "), num_series),
//                        new HBox(new Label("Descanso entre series: "), desc_serie),
//                        addSession
//                    )));
//        dialog.getDialogPane().setHeaderText("Nueva Sesión");
//        dialog.setY(300);
//        dialog.setTitle("Novo gropo");
//        addSession.setOnAction(e -> {
//            try {
//                SesionTipo sesion = new SesionTipo();
//                sesion.setCodigo(new_name.getText());
//                sesion.setT_calentamiento(Integer.parseInt(tiempo_calentamiento.getText()));
//                sesion.setNum_ejercicios(Integer.parseInt(num_ejer.getText()));
//                sesion.setT_ejercicio(Integer.parseInt(tiempo_ejer.getText()));
//                sesion.setD_ejercicio(Integer.parseInt(desc_ejer.getText()));
//                sesion.setNum_circuitos(Integer.parseInt(num_series.getText()));
//                sesion.setD_circuito(Integer.parseInt(desc_serie.getText()));
//                listSessionTypes.add(sesion);
//            }
//            catch(Exception err) {
//                System.out.println("Pollito la liaste");
//            }
//           });
//        ButtonType loginButtonType = new ButtonType("Login", ButtonBar.ButtonData.YES);
//        ButtonType cancelButtonType = ButtonType.CANCEL;
//        dialog.getDialogPane().getButtonTypes().addAll(loginButtonType, cancelButtonType);
//        Button btOk = (Button) dialog.getDialogPane().lookupButton(loginButtonType);
//        FXMLLoader loader = new FXMLLoader(getClass().getResource("path/to/fxml"));
//        loader.setRoot(this);
//        loader.setController(this);
//        FXMLChronoController controller = loader.getController();
//        btOk.addEventFilter(ActionEvent.ACTION, e -> {
//            try {
//                dialog.getDialogPane().setContent(loader.load());
//            } catch (IOException ex) {
//                System.out.println("error loading");
//            }
//            
//            e.consume();
//        });
//        dialog.showAndWait();
//
//
//    }
     @FXML
    private void beginWorkout(ActionEvent event) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("FXMLChrono.fxml"));
        Parent root = (Parent) fxmlLoader.load();
        Stage stage = new Stage();
        stage.setScene(new Scene(root));  
        stage.show();
            
    }
    
    
    void exitApplication() {  try {
            Alert alert = new Alert(AlertType.INFORMATION);
            alert.setHeaderText("Saving data in DB");
            alert.setContentText("The application is saving the changes in the data into the database. This action can expend some minutes.");
            AccesoBD.getInstance().salvar();
            alert.show();
            Platform.exit();
        } catch (Exception e) {
        }
    }

}
