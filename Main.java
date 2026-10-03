import java.util.ArrayList;
import java.util.Scanner;

public class Main {

    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        Gestor gestor = Gestor.getGestor();

        long inicio = System.nanoTime();
        gestor.cargarDatos();
        long ms = (System.nanoTime() - inicio) / 1_000_000;
        System.out.println("Se han cargado los datos en : " + ms + " ms");

        int opcion = -1;

        do {
            System.out.println("\n------------------- MENU -------------------");
            System.out.println("1. Comprobar existencia de un actor por nombre completo");
            System.out.println("2. Registrar nuevo actor");
            System.out.println("3. Devolver (no imprimir) las peliculas de un actor");
            System.out.println("4. Devolver (no imprimir) los actores de una pelicula");
            System.out.println("5. Modificar año de estreno de una pelicula");
            System.out.println("6. Eliminar un actor del sistema");
            System.out.println("7. Exportar datos a fichero");
            System.out.println("8. Obtener lista de actores ordenada alfabeticamente");
            System.out.println("0. Salir");
            System.out.print("\nSelecciona una opción: ");

            try {
                opcion = Integer.parseInt(sc.nextLine());
                String nom;

                switch (opcion) {
                    case 1:
                        System.out.print("Introduzca el nombre exacto que desea buscar: ");
                        nom = sc.nextLine();
                        long t1 = System.nanoTime();
                        Actor actorBuscado = gestor.buscarActor(nom);
                        long t1_fin = (System.nanoTime() - t1) / 1_000_000;
                        if (actorBuscado != null) {
                            System.out.println("El actor '" + nom + "' existe. (Búsqueda en " + t1_fin + " ms)");
                        } else {
                            System.out.println("No se ha encontrado a '" + nom + "'.");
                        }
                        break;

                    case 2:
                        System.out.print("Introduzca el ID del nuevo actor: ");
                        String id = sc.nextLine();
                        System.out.print("Introduzca el nombre del nuevo actor: ");
                        String nombreActor = sc.nextLine();

                        Actor nuevoActor = new Actor(id, nombreActor);
                        gestor.insertarActor(nuevoActor);
                        System.out.println("Actor anadido.");
                        break;

                    case 3:
                        System.out.print("Introduzca el actor: ");
                        nom = sc.nextLine();
                        if (gestor.buscarActor(nom) != null) {
                            long t3 = System.nanoTime();
                            ArrayList pelis = gestor.peliculasActor(nom);
                            long t3_fin = (System.nanoTime() - t3);
                            System.out.println("Operación completada en " + t3_fin + " ns. Se han devuelto " + pelis.size() + " películas.");
                        } else {
                            System.out.println("Actor no encontrado.");
                        }
                        break;

                    case 4:
                        System.out.print("Introduzca la pelicula: ");
                        nom = sc.nextLine();
                        try {
                            long t4 = System.nanoTime();
                            ArrayList reparto = gestor.actoresPelicula(nom);
                            long t4_fin = (System.nanoTime() - t4);
                            if (reparto != null) {
                                System.out.println("Operación completada en " + t4_fin + " ns. Se han devuelto " + reparto.size() + " actores.");
                            } else {
                                System.out.println("Película sin actores registrados.");
                            }
                        } catch (NullPointerException e) {
                            System.out.println("No se ha encontrado la película.");
                        }
                        break;

                    case 5:
                        System.out.print("Introduzca la pelicula que desea modificar: ");
                        nom = sc.nextLine();
                        System.out.print("¿Qué año de estreno quiere poner?: ");
                        try {
                            int anio = Integer.parseInt(sc.nextLine());
                            gestor.cambiarAnio(nom, anio);
                            System.out.println("Año modificado correctamente a " + anio);
                        } catch (NumberFormatException e) {
                            System.out.println("Error: Debe ingresar un año válido.");
                        } catch (NullPointerException e) {
                            System.out.println("Error: No se ha encontrado la película.");
                        }
                        break;

                    case 6:
                        System.out.print("¿Qué actor desea eliminar?: ");
                        nom = sc.nextLine();
                        if (gestor.buscarActor(nom) != null) {
                            long t6 = System.nanoTime();
                            gestor.borrarActor(nom);
                            long t6_fin = (System.nanoTime() - t6) / 1_000_000;
                            System.out.println("Actor eliminado correctamente del sistema en " + t6_fin + " ms.");
                        } else {
                            System.out.println("Error: El actor no existe.");
                        }
                        break;

                    case 7:
                        System.out.print("Introduzca el nombre del fichero destino (ej. actores.txt): ");
                        String rutaFichero = sc.nextLine();
                        gestor.guardarActores(rutaFichero);
                        System.out.println("Datos guardados en " + rutaFichero);
                        break;

                    case 8:
                        System.out.println("Generando lista ordenada...");
                        long t8 = System.nanoTime();
                        ArrayList listaOrdenada = gestor.obtenerActoresOrdenados();
                        long t8_fin = (System.nanoTime() - t8) / 1_000_000;
                        System.out.println("Operación completada en " + t8_fin + " ms. Se ha devuelto la lista con " + listaOrdenada.size() + " elementos.");
                        break;

                    case 0:
                        System.out.println("Saliendo...");
                        break;

                    default:
                        System.out.println("Opción no válida.");
                        break;
                }
            } catch (NumberFormatException e) {
                System.out.println("Error: Debe ingresar un número entero válido.");
                opcion = -1;
            }
        } while (opcion != 0);

        sc.close();
    }
}
