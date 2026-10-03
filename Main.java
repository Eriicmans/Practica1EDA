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
        System.out.println("------------------- MENU -------------------");
        System.out.println("1. Comprobar existencia de un actor por nombre completo");
        System.out.println("2. Registrar nuevo actor");
        System.out.println("3. Obtener las películas de un actor");
        System.out.println("4. Obtener el reparto (actores) de una película");
        System.out.println("5. Modificar año de estreno de una película");
        System.out.println("6. Eliminar un actor del sistema");
        System.out.println("7. Exportar datos a fichero");
        System.out.println("8. Obtener lista de actores ordenada alfabéticamente");


        do {
            System.out.print("\nSelecciona una opción: ");
            try {
                opcion = Integer.parseInt(sc.nextLine());
                String nom;

                switch (opcion) {
                    case 1:
                        System.out.print("Introduzca el nombre que desea buscar: ");
                        nom = sc.nextLine();
                        gestor.buscarActor(nom);
                        break;

                    case 2:
                        // Añadir actor
                        break;

                    case 3:
                        // Buscar las películas de un actor
                        System.out.print("Introduzca el actor: ");
                        nom = sc.nextLine();
                        gestor.peliculasActor(nom);
                        break;
                    case 4:
                        System.out.print("Introduzca la pelicula: ");
                        nom = sc.nextLine();
                        gestor.actoresPelicula(nom);
                        break;
                    case 5:
                        // Modificar año de estreno de una película
                        System.out.print("Introduzca la pelicula: ");
                        nom = sc.nextLine();
                        System.out.print("Que año quiere poner? ");
                        int anio = Integer.parseInt(sc.nextLine());
                        gestor.cambiarAnio(nom, anio);
                        break;

                    case 6:
                        // Borrar un actor
                        System.out.print("Que actor desea eliminar? ");
                        nom = sc.nextLine();
                        gestor.borrarActor(nom);
                        break;

                    case 7:
                        System.out.print("Introduce el nombre del fichero para guardar los actores (ej: actores.txt): ");
                        String nombreFichero = sc.nextLine();
                        gestor.guardarActores(nombreFichero);
                        System.out.println("Datos guardados correctamente en " + nombreFichero);
                        break;

                    case 8:
                        // Lista de actores ordenada por nombre
                        break;

                    case 0:
                        System.out.println("Saliendo...");
                        break;
                }
            } catch (NumberFormatException e) {
                System.out.println("Error: Debe ingresar un número entero válido.");
                opcion = -1;
            }
            System.out.println("Pulse 0 para abandonar");
        } while (opcion != 0);
    }
}
