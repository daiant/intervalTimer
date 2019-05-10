/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package intervaltimer;

import java.awt.event.ActionListener;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Accordion;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.text.Text;

/**
 * FXML Controller class
 *
 * @author pff
 */
public class FXMLChronoController implements Initializable,Runnable,ActionListener {

    @FXML
    private Text tiempo;

    public static int onoff = 0;
    boolean pausar;
    Thread hilo;
    boolean cronometroActivo;
    @FXML
    private Accordion asd;
    Integer minutos;
    Integer segundos;
    Integer milesimas;
    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // TODO
        minutos = 1;
        segundos = 0;
        milesimas = 0;
    }    

    public void run() {
        //min es minutos, seg es segundos y mil es milesimas de segundo
        String min = "", seg = "", mil = "";
        try {
            //Mientras cronometroActivo sea verdadero entonces seguira
            //aumentando el tiempo
            while (cronometroActivo) {
                Thread.sleep(100);
                //Cuando llega a 1000 osea 1 segundo aumenta 1 segundo
                //y las milesimas de segundo de nuevo a 0
                if (milesimas == 0) {
                    
                    //Si los segundos llegan a 60 entonces aumenta 1 los minutos
                    //y los segundos vuelven a 0
                    if (segundos == 0 && minutos == 0 && milesimas == 0) {
                        segundos = 0;
                        System.out.println("FINISH");
                        pararCronometro();
                        minutos--;
                    }
                    if(segundos == 0){
                        segundos = 59;
                        minutos -= 1;
                        
                    }
                    milesimas = 1000;
                    segundos -= 1;
                }
                milesimas -= 100;

                //Esto s para que siempre este en formato
                //00:00:000
                if (minutos < 10) {
                    min = "0" + minutos;
                } else {
                    min = minutos.toString();
                }
                if (segundos < 10) {
                    seg = "0" + segundos;
                } else {
                    seg = segundos.toString();
                }

                if (milesimas < 10) {
                    mil += milesimas.toString();
                } else if (milesimas < 100) {
                    mil =  milesimas.toString();
                } else {
                    mil = milesimas.toString();
                }

                //Colocamos en la etiqueta la informacion

                tiempo.setText(min + ":" + seg + ":" + mil);
            }
        } catch (Exception e) {
        }
        //Cuando se reincie se coloca nuevamente en 00:00:000
        tiempo.setText("00:00:00");
    }
    
   
    public void iniciarCronometro() {
        cronometroActivo = true;
        pausar = true;
        hilo = new Thread( this );
        hilo.start();
    }
    public void pararCronometro(){
        cronometroActivo = false;
        pausar = true ;
    }
    public void reiniciarCronometro() {
        cronometroActivo = false;
        pausar = false;
    }

    @FXML
    private void start(ActionEvent event) {
        iniciarCronometro();
    }


    @FXML
    private void stop(ActionEvent event) {
                pararCronometro();
    }

    @FXML
    private void restart(ActionEvent event) {
        reiniciarCronometro();
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
    
}
