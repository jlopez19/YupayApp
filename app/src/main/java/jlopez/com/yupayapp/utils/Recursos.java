package jlopez.com.yupayapp.utils;

/**
 * Created by Belal on 8/25/2017.
 */
public class Recursos {

    private String tema;
    private String url;
    private String type;
    private String grado;
    private String tipo_aprendizaje;

    public String getTema() {
        return tema;
    }

    public void setTema(String tema) {
        this.tema = tema;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getGrado() {
        return grado;
    }

    public void setGrado(String grado) {
        this.grado = grado;
    }

    public String getTipo_aprendizaje() {
        return tipo_aprendizaje;
    }

    public void setTipo_aprendizaje(String tipo_aprendizaje) {
        this.tipo_aprendizaje = tipo_aprendizaje;
    }

    public Recursos(String tema, String url, String type, String grado, String tipo_aprendizaje) {
        this.tema = tema;
        this.url = url;
        this.type = type;
        this.grado = grado;
        this.tipo_aprendizaje = tipo_aprendizaje;
    }


    // Default constructor required for calls to
    // DataSnapshot.getValue(User.class)
    public Recursos() {
    }


}