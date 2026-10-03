import java.util.ArrayList;
public class Pelicula {

    private String id;
    private String nombre;
    private int anio;
    private ArrayList<Actor> actores;

    public Pelicula(String id, String nombre, int anio){
        this.id = id;
        this.nombre = nombre;
        this.anio = anio;
        this.actores = new ArrayList<>();
    }
    public void anadirActor(Actor actor){
        actores.add(actor);
    }
    public ArrayList<Actor> getActores() {
        return actores;}
    public void setAnio(int anio){
        this.anio = anio;
    }
    public void borrarActor(Actor actor){
        actores.remove(actor);
    }
}
