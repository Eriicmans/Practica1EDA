import java.util.ArrayList;

public class Pelicula {

    private String nombre;
    private String idPelicula;
    private ArrayList<Actor> listaActores;
    private int anio;

    public Pelicula(String nombre, String idPelicula, int anio) {
        this.nombre = nombre;
        this.idPelicula = idPelicula;
        this.listaActores = new ArrayList<Actor>();
        this.anio = anio;
    }

    public void aniadirActor(Actor actor) {

        listaActores.add(actor);
    }

    public String getNombre() {
        return this.nombre;
    }

    public int getAnio() {
        return this.anio;
    }

    public void imprimirActores() {
        int cont = 0;
        for (Actor actor : this.listaActores) {
            cont++;
            System.out.println(cont + ". " + actor.getNombre());
        }
    }

    public void setAnio(int anio) {
        this.anio = anio;
    }
}
