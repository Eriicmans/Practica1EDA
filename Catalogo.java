import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Scanner;

public class Catalogo {

    private static Catalogo catalogo;
    private HashMap<String, Actor> actores;
    private HashMap<String, Pelicula> peliculas;


    private Catalogo() {
        this.actores = new HashMap<String, Actor>();
        this.peliculas = new HashMap<String, Pelicula>();
    }

    public static Catalogo getCatalogo() {
        if (catalogo == null) {
            catalogo = new Catalogo();
        }
        return catalogo;
    }

    public void cargarDatos() {
        Catalogo catalogo = Catalogo.getCatalogo();

        System.out.println("Cargando datos...");

        for (int anio = 1970; anio <= 2023; anio++) {
            this.readFile("movies-dir/actors_and_films_" + anio + ".txt", anio);
        }

        this.imprimirEstadisticas();
    }

    public void readFile(String nom, int anio) {
        try {
            Scanner entrada = new Scanner(new FileReader(nom));

            String linea;
            while (entrada.hasNext()) {
                linea = entrada.nextLine();
                cargarDatosArchivo(linea, anio);
            }

            entrada.close();
        } // try
        catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void cargarDatosArchivo(String linea, int anio) {
        String[] datos = linea.split(" ### ");

        String nombreCompleto = datos[1];


        String[] datos2 = datos[0].split("/Q");
        String idActor = datos2[1];

        String nombrePeli = datos[3];

        String[] datos3 = datos[2].split("/Q");
        String idPeli = datos3[1];

        // Instancia de actores

        Actor actor = actores.get(nombreCompleto);
        if (actor == null) {
            actor = new Actor(nombreCompleto, idActor);
            actores.put(nombreCompleto, actor);
        }


        // Instancia de pelis

        Pelicula peli = peliculas.get(nombrePeli);
        if (peli == null) {
            peli = new Pelicula(nombrePeli, idPeli, anio);
            peliculas.put(nombrePeli, peli);
        }

        // Añadir datos
        actor.aniadirPeli(peli);
        peli.aniadirActor(actor);


    }

    public void buscarActorPorNombre(String nombreActor) {
        Actor actor = actores.get(nombreActor);

        if (actor != null) actor.mostrarInfoActor();
        else System.out.println("No existe actor con esos nombres y apellidos");

    }

    public void buscarPeliculasActor(String nombreActor) {
        Actor actor = actores.get(nombreActor);

        if (actor != null) actor.imprimirPeliculas();
        else System.out.println("No existe actor con esos nombres y apellidos");
    }

    public void buscarActoresPelicula(String nombrePelicula) {
        Pelicula peli = peliculas.get(nombrePelicula);

        if (peli != null) peli.imprimirActores();
        else System.out.println("No se ha encontrado una pelicula con ese nombre");
    }

    public void imprimirEstadisticas() {
        System.out.println("Total actores Unicos: " + this.actores.size());
        System.out.println("Total peliculas Unicas: " + this.peliculas.size());
    }

    public void cambiarAnioPelicula(String nombrePelicula, int anio) {
        Pelicula peli = peliculas.get(nombrePelicula);

        if (peli != null) peli.setAnio(anio);
        else System.out.println("No se ha encontrado una pelicula con ese nombre");
    }

    public void eliminarActor(String nombreActor) {
        actores.remove(nombreActor);
    }
}
