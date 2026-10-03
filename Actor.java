import java.util.ArrayList;

public class Actor {
    private String id;
    private String nombre;
    private ArrayList<Pelicula> peliculas;

    public Actor(String id, String nombre){
        this.id = id;
        this.nombre = nombre;
        this.peliculas = new ArrayList<>();

    }
    public void anadirPelicula(Pelicula pelicula){
        peliculas.add(pelicula);
    }
    public String getNombre(){
        return nombre;
    }

    public ArrayList<Pelicula> getPeliculas() {
        return peliculas;
    }
}
