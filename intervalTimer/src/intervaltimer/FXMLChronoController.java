/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package intervaltimer;

import accesoBD.AccesoBD;
import javafx.scene.paint.Color;
import java.awt.event.ActionListener;
import java.net.URL;
import java.time.Duration;
import java.time.LocalTime;
import java.time.format.FormatStyle;
import java.util.ResourceBundle;
import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Accordion;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Spinner;
import javafx.scene.control.SpinnerValueFactory;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.util.converter.LocalTimeStringConverter;
import modelo.Gym;
import modelo.SesionTipo;
import org.omg.CORBA.portable.ValueFactory;

/**
 * FXML Controller class
 *
 * @author pff
 */
public class FXMLChronoController implements Initializable, Runnable, ActionListener {

    @FXML
    private Text tiempo;

    public static int onoff = 0;
    boolean reset = false;
    boolean esCal = true;
    boolean esDesc = false;
    boolean running = false;
    boolean reiniciar = false;
    
    Long iniTime;
    Long finTime;
    boolean pausar;
    Thread hilo;
    boolean cronometroActivo;
    Integer tiempoEj;
    Integer dEj;
    Integer tiempoCal;
    Integer tiempoDescSerie;
    Integer numSeries;
    Integer numEjercicios;
    Integer minutos;
    Integer segundos;
    Integer milesimas;
    Integer ejActual = 1;
    Integer serieActual = 1;
    boolean siguienteEj = false;
    boolean siguienteSer = false;
    
    @FXML
    private TextField t_calentamiento;
    @FXML
    private TextField n_ejercicio;
    @FXML
    private TextField t_ejercicio;
    @FXML
    private TextField d_ejercicio;
    @FXML
    private TextField n_series;
    @FXML
    private TextField d_series;
    @FXML
    private Button btnStart;
    @FXML
    private Button btnCancelTabla;
    @FXML
    private Button addSesion;
    @FXML
    private TextField textId;
    @FXML
    private VBox vBoxSesiones;
    @FXML
    private VBox vBoxChrono;

    private ObservableList<SesionTipo> listSessionTypes;
    @FXML
    private TableView<SesionTipo> tableSessionTypes;
    @FXML
    private TableColumn<SesionTipo, String> nameColumn;
    @FXML
    private TableColumn<SesionTipo, String> seriesColumn;
    @FXML
    private TableColumn<SesionTipo, String> ejerciciosColumn;
    @FXML
    private TableColumn<SesionTipo, String> tiemposColumn;
    @FXML
    private TableColumn<SesionTipo, String> descansoColumn;
    @FXML
    private Text textEjercicio;
    @FXML
    private Text textSerie;
    @FXML
    private Button btnNextEj;
    @FXML
    private Button btnNextSerie;
    @FXML
    private Text type;

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
  
        // TODO
        Gym gym = AccesoBD.getInstance().getGym();
        listSessionTypes = FXCollections.observableList(gym.getTiposSesion());
        tableSessionTypes.setItems(listSessionTypes);
        nameColumn.setCellValueFactory(
                new PropertyValueFactory<>("codigo")
        );
        seriesColumn.setCellValueFactory(new PropertyValueFactory<>("num_circuitos"));
        ejerciciosColumn.setCellValueFactory(new PropertyValueFactory<>("num_ejercicios"));
        tiemposColumn.setCellValueFactory(new PropertyValueFactory<>("t_ejercicio"));
        descansoColumn.setCellValueFactory(new PropertyValueFactory<>("d_ejercicio"));
        addSesion.setOnAction(e -> {
            try {
                SesionTipo sesion = new SesionTipo();
                sesion.setCodigo(textId.getText());
                sesion.setT_calentamiento(Integer.parseInt(t_calentamiento.getText()));
                sesion.setNum_ejercicios(Integer.parseInt(n_ejercicio.getText()));
                sesion.setT_ejercicio(Integer.parseInt(t_ejercicio.getText()));
                sesion.setD_ejercicio(Integer.parseInt(d_ejercicio.getText()));
                sesion.setNum_circuitos(Integer.parseInt(n_series.getText()));
                sesion.setD_circuito(Integer.parseInt(d_series.getText()));
                listSessionTypes.add(sesion);
            } catch (Exception err) {
                System.out.println(e);
            }
        });
        btnStart.disableProperty().bind(Bindings.isEmpty(tableSessionTypes.getSelectionModel().getSelectedItems()));
        btnStart.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent e) {
                if (tableSessionTypes.getSelectionModel().getSelectedItem() != null) {
                    vBoxSesiones.setVisible(false);
                    vBoxChrono.setVisible(true);
                    //----------------------
                    SesionTipo sesion = tableSessionTypes.getSelectionModel().getSelectedItem();
                    dEj = sesion.getD_ejercicio();
                    tiempoEj = sesion.getT_ejercicio();
                    tiempoCal = sesion.getT_calentamiento();
                    tiempoDescSerie = sesion.getD_circuito();
                    numSeries = sesion.getNum_circuitos();
                    numEjercicios = sesion.getNum_ejercicios();
                    System.out.println("hola");
                    setIniText();
                }
                //btnStart.setDisable(true);
            }
        });
        tiempoCal = 10;
        dEj = 11;
        tiempoEj = 9;
    }

    public void run() {
        //min es minutos, seg es segundos y mil es milesimas de segundo

        segundos = 10;
        minutos = 0;
        milesimas = 0;
        String min = "", seg = "", mil = "";
        for (int j = 1; j <= numSeries;) {
            textSerie.setText("Serie : " + j + "/"  + numSeries);
            reset = false;
            for (int i = 0; i < numEjercicios;) {
                if(siguienteSer){
                    siguienteSer = false;
                    break;
                }
                if (reset) {
                    break;
                }

                cronometroActivo = true;
                if (esCal) {
                    segundos = 0;
                    minutos = tiempoCal;
                    milesimas = 0;
                    System.out.println(minutos + " " + segundos + " " + milesimas);
                } else {

                    if (esDesc) {
                        if (j == 0) {
                            segundos = 0;
                            minutos = dEj;
                            milesimas = 0;
                            System.out.println(minutos + " " + segundos + " " + milesimas);
                        } else {
                            segundos = 0;
                            minutos = tiempoDescSerie;
                            milesimas = 0;
                        }
                    } else {
                        segundos = 9;
                        minutos = tiempoEj;
                        milesimas = 0;
                        ejActual++;
                        System.out.println(minutos + " " + segundos + " " + milesimas);
                    }

                }
                try {
                    while (cronometroActivo) {
                        if(siguienteEj){
                            siguienteEj = false;
                            break;
                        }
                        if(siguienteSer){
                            break;
                        }
                        Thread.sleep(100);
                        if (reset) {
                            i = 0;
                            j = 0;
                            break;
                        }
                        if (minutos == 0 && segundos <= 10) {
                            //System.out.println("HOla!!!!!!!!!!!!");
                            tiempo.setFill(Color.RED);
                        }
                        else tiempo.setFill(Color.BLACK);
                        if (milesimas == 0) {
                            //Si los segundos llegan a 60 entonces aumenta 1 los minutos
                            //y los segundos vuelven a 0
                            if (segundos <= 0 && minutos == 0 && milesimas == 0) {
                                System.out.println("FINISH");
                                ejActual += 1;
                                pararCronometro();
                                break;
                            }
                            if (segundos <= 0) {
                                segundos = 59;
                                minutos -= 1;
                            }
                            milesimas = 1000;
                            segundos -= 1;
                        }
                        milesimas -= 100;

                        //Esto s para que siempre este en formato
                        //00:00:000
                        String m = String.format("%02d", minutos);
                        String s = String.format("%02d", segundos);
                        String mi = String.format("%03d", milesimas);
                        if(pausar) 
                            while(pausar){
                                Thread.sleep(100);
                            }
                        //Colocamos en la etiqueta la informacion
                        tiempo.setText(m + ":" + s + ":" + mi);
                    }
                    System.out.println("esto deberia terminar?");

                    if (esCal) {
                        esCal = !esCal;
                        esDesc = false;
                    } else {
                        esDesc = !esDesc;
                        if (!esDesc) {
                            System.out.println("Esto ha sido un descanso");
                        }
                    }
                    if (!esDesc && !esCal) {
                        i++;
                        textEjercicio.setText("Ejercicio : " + i + "/" + numEjercicios);
                    }
                    
                    // this.run();
                } catch (Exception e) {
                    System.out.println(e);
                }

            }
            if(siguienteSer){
                    siguienteSer = false;
                   // break;
            }
            esCal = true;
            j++;
           
            
        }
//-+----------------------------------------------------------------//
        // Aquí acaba el cronometro
        finTime = System.currentTimeMillis();
        Duration duration = Duration.ofMillis(finTime - iniTime); // A segundos.
        System.out.println(finTime - iniTime);
        System.out.println(duration.toMinutes());
        
        tiempo.setText("00:00:000");
    }

    public void iniciarCronometro() {
        if(!cronometroActivo)
            iniTime = System.currentTimeMillis();
        cronometroActivo = true;
        pausar = false;
        if(!running){
            if(hilo != null) hilo.stop();
            hilo = new Thread(this);
            hilo.start();
        }   
        running = true;
    }

    public void pararCronometro() {
        cronometroActivo = true;
        pausar = !pausar;
    }

    public void reiniciarCronometro() {
        reset =true;
        pausar = false;
         ejActual = 1; 
        serieActual = 1; 
        setIniText();
        pausar = true;
        running = false;
        ejActual = 0;
        serieActual = 0;
    }
    public void siguiente(int i){
        i++;
    }
    

    @FXML
    private void stop(ActionEvent event) {
        pararCronometro();
//        btnStart.setDisable(true);
    }

    @FXML
    private void restart(ActionEvent event) {
        reiniciarCronometro();
 //       btnStart.setDisable(false);
    }

    @FXML
    private void start(ActionEvent event) {
        iniciarCronometro();
//        btnStart.setDisable(true);
    }

    @Override
    public void actionPerformed(java.awt.event.ActionEvent ae) {
        Object o = ae.getSource();
        if (o instanceof Button) {
            Button btn = (Button) o;
            if (btn.getText().equals("Start")) {
                iniciarCronometro();
            }
            if (btn.getText().equals("Restart")) {
                reiniciarCronometro();
            }
            if (btn.getText().equals("Stop")) {
                if (cronometroActivo) {
                    pararCronometro();
                } else {
                    cronometroActivo = true;
                }
            }
        }
    }

    @FXML
    private void closeWindow(ActionEvent event) {
        Stage stage = (Stage) btnCancelTabla.getScene().getWindow();
        stage.close();
    }

    @FXML
    private void siguienteEj(ActionEvent event) {
        siguienteEj = true;
    }

    @FXML
    private void siguienteSer(ActionEvent event) {
        siguienteSer = true;
        }
    private void setIniText() {
        System.out.println("aldñfdkj");
            minutos = Math.toIntExact(tiempoCal % 60);
            segundos = Math.toIntExact(tiempoCal / 60);
            milesimas = 0;
            String m = String.format("%02d", minutos);
            String s = String.format("%02d", segundos);
            String mil = String.format("%03d", milesimas);

            textEjercicio.setText("Ejercicio : " + ejActual + "/" + numEjercicios);
            textSerie.setText("Serie :" + serieActual + "/" + numSeries);
            tiempo.setText(m + ":" + s + ":" + mil);
        }

   
}
