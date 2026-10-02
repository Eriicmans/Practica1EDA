import java.util.ArrayList;

public class Actor {

    private String nombreCompleto;
    private String idActor;
    private ArrayList<Pelicula> listaPeliculas;

    public Actor(String nombreCompleto, String idActor) {
        this.nombreCompleto = nombreCompleto;
        this.idActor = idActor;
        this.listaPeliculas = new ArrayList<Pelicula>();
    }

    public void aniadirPeli(Pelicula p) {
        if (!this.listaPeliculas.contains(p)) this.listaPeliculas.add(p);
    }

    public String getNombre() {
        return this.nombreCompleto;
    }

    public void imprimirPeliculas() {
        int cont = 0;
        System.out.println(this.getNombre() + " tiene un total de " + this.listaPeliculas.size() + " peliculas");
        for (Pelicula peli : this.listaPeliculas) {
            cont++;
            System.out.println(cont + ". " + peli.getNombre() + " | Ano: " + peli.getAnio());
        }
    }

    public void mostrarInfoActor() {
        System.out.println("Nombre Completo: " + this.nombreCompleto);
        System.out.println("Id del actor: " + this.idActor);
        System.out.println("Lista de peliculas: ");

        this.imprimirPeliculas();
    }
}
