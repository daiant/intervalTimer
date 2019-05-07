/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package intervaltimer;

import accesoBD.AccesoBD;
import java.net.URL;
import java.util.Optional;
import java.util.ResourceBundle;
import javafx.beans.binding.Bindings;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.control.DialogPane;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import modelo.Grupo;
import modelo.Gym;

/**
 *
 * @author daiant
 */
public class FXMLIntervalControllerController implements Initializable {
    
    private ObservableList<Grupo> listGroups;
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

    @FXML
    private void beginWorkout(ActionEvent event) {
    }
    
}
