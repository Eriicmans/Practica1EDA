import java.util.HashMap;
import java.util.Scanner;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.io.PrintWriter;

public class Gestor {
    private static Gestor gestor;
    private HashMap<String,Actor> actores;
    private HashMap<String,Pelicula> peliculas;

    private Gestor(){
        actores = new HashMap<>();
        peliculas = new HashMap<>();
    }

    public static Gestor getGestor() {
        if (gestor == null) {
            gestor = new Gestor();
        }
        return gestor;
    }


    public void cargarDatos(){
        File carpeta = new File("movies-dir");
        File[] ficheros = carpeta.listFiles();
        for(File f: ficheros){
            String nombre = f.getName();
            String[] partes = nombre.split("_");
            String anioConTexto = partes[3];
            String [] partesAnio = anioConTexto.split("\\.");
            String anioFinal = partesAnio[0];
            int anio = Integer.parseInt(anioFinal);

            String ruta = f.getPath();

            cargarFichero(ruta, anio);
        }
        System.out.println("Actores cargados: " + actores.size());
        System.out.println("Peliculas cargadas: " + peliculas.size());
    }


    public void cargarFichero(String ruta, int anio) {
        File fichero = new File(ruta);

        try{
            Scanner sc = new Scanner(fichero);
            while(sc.hasNextLine()){
                String linea = sc.nextLine();

                String datos[] = linea.split("\\s*###\\s*");

                String idActor = datos[0];
                String nombreActor = datos[1];
                String idPelicula = datos[2];
                String nombrePelicula = datos[3];

                Actor actor;
                if(actores.containsKey(nombreActor)){
                    actor = actores.get(nombreActor);
                }
                else{
                    actor = new Actor(idActor,nombreActor);
                    actores.put(nombreActor, actor);}


                Pelicula pelicula;
                if(peliculas.containsKey((nombrePelicula))){
                    pelicula = peliculas.get(nombrePelicula);
                }
                else{
                    pelicula = new Pelicula(idPelicula,nombrePelicula,anio);
                    peliculas.put(nombrePelicula,pelicula);
                }
                actor.anadirPelicula(pelicula);
                pelicula.anadirActor(actor);
            }
            sc.close();
        }
        catch(FileNotFoundException e){
            System.out.println("Fichero no encontrado");
        }
    }
    public Actor buscarActor(String nombreActor){
        return actores.get(nombreActor);
    }

    public void insertarActor(Actor actor){
        if(actores.containsKey(actor.getNombre())){
            return;
        }
        actores.put(actor.getNombre(),actor);
    }
    public ArrayList<Pelicula> peliculasActor(String nombre){
        Actor actor = actores.get(nombre);
        return actor.getPeliculas();
    }
    public ArrayList<Actor> actoresPelicula(String nombre){
        Pelicula pelicula = peliculas.get(nombre);
        return pelicula.getActores();
    }
    public void cambiarAnio(String nombrePeli, int nuevoAnio){
        peliculas.get(nombrePeli).setAnio(nuevoAnio);
    }

    public void borrarActor(String nombre){
        Actor actor = actores.get(nombre);
        for(Pelicula pelicula: actor.getPeliculas()){
            pelicula.borrarActor(actor);
        }
        actores.remove(nombre);
    }
    public void guardarActores(String nombre){
        try{
            PrintWriter fichero = new PrintWriter(nombre);
            for(Actor actor : actores.values()){
                fichero.println(actor.getNombre());
            }
            fichero.close();
        }catch(FileNotFoundException e){
            System.out.println("El fichero no se ha podido crear");
        }
    }
    public ArrayList<Actor> obtenerActoresOrdenados() {
        ArrayList<Actor> lista = new ArrayList<>(actores.values());

        lista.sort((a1, a2) -> a1.getNombre().compareTo(a2.getNombre()));

        return lista;
    }
}
