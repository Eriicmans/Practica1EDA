import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assumptions.*;

import java.io.*;
import java.lang.reflect.Field;
import java.nio.file.*;
import java.util.*;

import org.junit.jupiter.api.*;

// Pruebas unitarias de la clase Gestor
class GestorTest {

    // Carpeta temporal propia de cada prueba (para los ficheros de entrada y salida).
    private Path tmp;

    private Gestor gestor;

    // El Gestor es un singleton: se reinicia antes de cada prueba.
    @BeforeEach
    void reiniciarGestor() throws Exception {
        tmp = Files.createTempDirectory("gestorTest");
        Field f = Gestor.class.getDeclaredField("gestor");
        f.setAccessible(true);
        f.set(null, null);
        gestor = Gestor.getGestor();
    }

    // Borra la carpeta temporal (si Windows la tiene bloqueada, se ignora el fallo)
    @AfterEach
    void limpiar() {
        File[] ficheros = tmp.toFile().listFiles();
        if (ficheros != null) {
            for (File f : ficheros) {
                f.delete();
            }
        }
        tmp.toFile().delete();
    }

    // Crea un fichero de texto con las lineas indicadas y devuelve su ruta.
    private String fichero(String nombre, String... lineas) throws IOException {
        Path p = tmp.resolve(nombre);
        Files.write(p, List.of(lineas));
        return p.toString();
    }

    // Linea con el formato idActor ### nombreActor ### idPelicula ### nombrePelicula
    private static String linea(String idA, String actor, String idP, String peli) {
        return idA + " ### " + actor + " ### " + idP + " ### " + peli;
    }

    // Pelicula no tiene getAnio(): se lee el atributo privado por reflexion.
    private static int anioDe(Pelicula p) throws Exception {
        Field f = Pelicula.class.getDeclaredField("anio");
        f.setAccessible(true);
        return f.getInt(p);
    }

    private static boolean estaOrdenada(List<Actor> lista) {
        for (int i = 1; i < lista.size(); i++) {
            if (lista.get(i - 1).getNombre().compareTo(lista.get(i).getNombre()) > 0) {
                return false;
            }
        }
        return true;
    }

    @Nested
    class CargarDatosYCargarFichero {

        // Ejecucion de cargarDatos con el directorio de ficheros vacio.
        @Test
        void cargarDatos_directorioVacio() {
            File dir = new File("movies-dir");
            assumeFalse(dir.exists(), "movies-dir ya existe: prueba omitida");
            assertTrue(dir.mkdir());
            try {
                assertDoesNotThrow(() -> gestor.cargarDatos());
                assertEquals(0, gestor.obtenerActoresOrdenados().size());
            } finally {
                dir.delete();
            }
        }

        // Carga con cargarFichero de ficheros con lineas mal formateadas o campos faltantes.
        @Test
        void cargarFichero_lineasMalFormadasOCamposFaltantes() throws Exception {
            String sinSeparador = fichero("mala1.txt", "esto no tiene el formato correcto");
            assertThrows(ArrayIndexOutOfBoundsException.class, () -> gestor.cargarFichero(sinSeparador, 2000));

            String camposFaltantes = fichero("mala2.txt", "a1 ### Tom Hanks");
            assertThrows(ArrayIndexOutOfBoundsException.class, () -> gestor.cargarFichero(camposFaltantes, 2000));
        }

        // Carga de todos los ficheros validando que no se creen duplicados.
        @Test
        void cargarFicheros_noSeCreanActoresNiPeliculasDuplicados() throws Exception {
            gestor.cargarFichero(fichero("datos_1988.txt",
                    linea("a1", "Tom Hanks", "p1", "Big"),
                    linea("a2", "Robert Loggia", "p1", "Big")), 1988);
            gestor.cargarFichero(fichero("datos_1984.txt",
                    linea("a1", "Tom Hanks", "p2", "Splash"),
                    linea("a3", "Daryl Hannah", "p2", "Splash"),
                    linea("a1", "Tom Hanks", "p3", "Bachelor Party")), 1984);

            assertEquals(3, gestor.obtenerActoresOrdenados().size());
            Actor hanks = gestor.buscarActor("Tom Hanks");
            assertEquals(3, hanks.getPeliculas().size());

            Actor loggia = gestor.buscarActor("Robert Loggia");
            assertSame(hanks.getPeliculas().get(0), loggia.getPeliculas().get(0));
            assertEquals(2, gestor.actoresPelicula("Big").size());
            assertEquals(2, gestor.actoresPelicula("Splash").size());
        }

        // Carga completa con cargarDatos sobre la carpeta real de datos.
        @Test
        void cargarDatos_cargaCompleta_sinActoresDuplicados() {
            assumeTrue(new File("movies-dir").isDirectory(), "No existe movies-dir: prueba omitida");
            gestor.cargarDatos();
            ArrayList<Actor> actores = gestor.obtenerActoresOrdenados();
            assertTrue(actores.size() > 0);
            Set<String> nombres = new HashSet<>();
            for (Actor a : actores) {
                assertTrue(nombres.add(a.getNombre()), "Actor duplicado: " + a.getNombre());
            }
        }
    }

    @Nested
    class BuscarActor {

        // Busqueda de un actor que no existe en un sistema con datos cargados.
        @Test
        void actorQueNoExiste() throws Exception {
            gestor.cargarFichero(fichero("f.txt", linea("a1", "Tom Hanks", "p1", "Big")), 1988);
            assertNull(gestor.buscarActor("Meg Ryan"));
        }

        // Busqueda de un actor que si existe en el sistema.
        @Test
        void actorQueSiExiste() throws Exception {
            gestor.cargarFichero(fichero("f.txt", linea("a1", "Tom Hanks", "p1", "Big")), 1988);
            Actor a = gestor.buscarActor("Tom Hanks");
            assertNotNull(a);
            assertEquals("Tom Hanks", a.getNombre());
        }
    }

    @Nested
    class InsertarActor {

        // Insercion de un actor completamente nuevo.
        @Test
        void actorNuevo() {
            Actor a = new Actor("a1", "Tom Hanks");
            gestor.insertarActor(a);
            assertSame(a, gestor.buscarActor("Tom Hanks"));
        }

        // Intento de insercion de un actor cuyo nombre ya esta registrado (no se duplica ni sobreescribe).
        @Test
        void actorYaRegistrado_noSeDuplicaNiSobreescribe() {
            Actor original = new Actor("a1", "Tom Hanks");
            Actor repetido = new Actor("a99", "Tom Hanks");
            gestor.insertarActor(original);
            gestor.insertarActor(repetido);
            assertSame(original, gestor.buscarActor("Tom Hanks"));
            assertEquals(1, gestor.obtenerActoresOrdenados().size());
        }
    }

    @Nested
    class PeliculasActor {

        // Actor que existe, pero aun no tiene peliculas asignadas.
        @Test
        void actorSinPeliculas() {
            gestor.insertarActor(new Actor("a1", "Tom Hanks"));
            ArrayList<Pelicula> pelis = gestor.peliculasActor("Tom Hanks");
            assertNotNull(pelis);
            assertTrue(pelis.isEmpty());
        }

        // Actor con multiples peliculas en su historial.
        @Test
        void actorConVariasPeliculas() throws Exception {
            gestor.cargarFichero(fichero("f.txt",
                    linea("a1", "Tom Hanks", "p1", "Big"),
                    linea("a1", "Tom Hanks", "p2", "Splash"),
                    linea("a1", "Tom Hanks", "p3", "Cast Away")), 1990);
            assertEquals(3, gestor.peliculasActor("Tom Hanks").size());
        }
    }

    @Nested
    class ActoresPelicula {

        // Consultar una pelicula valida que tiene varios actores asociados.
        @Test
        void peliculaConVariosActores() throws Exception {
            gestor.cargarFichero(fichero("f.txt",
                    linea("a1", "Tom Hanks", "p1", "Splash"),
                    linea("a2", "Daryl Hannah", "p1", "Splash"),
                    linea("a3", "John Candy", "p1", "Splash")), 1984);
            ArrayList<Actor> reparto = gestor.actoresPelicula("Splash");
            assertEquals(3, reparto.size());
            assertTrue(reparto.contains(gestor.buscarActor("Tom Hanks")));
            assertTrue(reparto.contains(gestor.buscarActor("Daryl Hannah")));
            assertTrue(reparto.contains(gestor.buscarActor("John Candy")));
        }
    }

    @Nested
    class CambiarAnio {

        // Asignacion de un anio valido a una pelicula previamente cargada desde el fichero.
        @Test
        void anioValidoEnPeliculaCargada() throws Exception {
            gestor.cargarFichero(fichero("f.txt", linea("a1", "Tom Hanks", "p1", "Big")), 1988);
            Pelicula big = gestor.buscarActor("Tom Hanks").getPeliculas().get(0);
            assertEquals(1988, anioDe(big));

            gestor.cambiarAnio("Big", 2005);

            assertEquals(2005, anioDe(big));
        }
    }

    @Nested
    class BorrarActor {

        // Borrado de un actor sin peliculas asociadas.
        @Test
        void actorSinPeliculas() {
            gestor.insertarActor(new Actor("a1", "Tom Hanks"));
            gestor.borrarActor("Tom Hanks");
            assertNull(gestor.buscarActor("Tom Hanks"));
        }

        // Borrado de un actor que participa en decenas de peliculas: ya no figura en ninguna.
        @Test
        void actorConDecenasDePeliculas() throws Exception {
            String[] lineas = new String[60];
            for (int i = 0; i < 30; i++) {
                lineas[2 * i] = linea("a1", "Tom Hanks", "p" + i, "Pelicula " + i);
                lineas[2 * i + 1] = linea("a2", "Meg Ryan", "p" + i, "Pelicula " + i);
            }
            gestor.cargarFichero(fichero("f.txt", lineas), 1995);
            Actor hanks = gestor.buscarActor("Tom Hanks");
            assertEquals(30, hanks.getPeliculas().size());

            gestor.borrarActor("Tom Hanks");

            assertNull(gestor.buscarActor("Tom Hanks"));
            for (int i = 0; i < 30; i++) {
                ArrayList<Actor> reparto = gestor.actoresPelicula("Pelicula " + i);
                assertFalse(reparto.contains(hanks), "Sigue en Pelicula " + i);
                assertEquals(1, reparto.size()); // Meg Ryan sigue en el reparto
            }
        }
    }

    @Nested
    class GuardarActores {

        // Exportar los datos con un diccionario vacio.
        @Test
        void diccionarioVacio() throws Exception {
            Path salida = tmp.resolve("vacio_out.txt");
            gestor.guardarActores(salida.toString());
            assertTrue(Files.exists(salida));
            assertEquals(0, Files.readAllLines(salida).size());
        }

        // Exportar un diccionario cargado con miles de actores: la escritura finaliza.
        @Test
        void milesDeActores() throws Exception {
            for (int i = 0; i < 5000; i++) {
                gestor.insertarActor(new Actor("id" + i, "Actor " + i));
            }
            Path salida = tmp.resolve("miles_out.txt");
            gestor.guardarActores(salida.toString());
            assertEquals(5000, Files.readAllLines(salida).size());
        }
    }

    @Nested
    class ObtenerActoresOrdenados {

        // Obtener la lista con un diccionario vacio.
        @Test
        void diccionarioVacio() {
            assertTrue(gestor.obtenerActoresOrdenados().isEmpty());
        }

        // Obtener la lista con un diccionario cargado con miles de actores: termina y esta ordenada.
        @Test
        void milesDeActores() {
            Random r = new Random(42);
            for (int i = 0; i < 5000; i++) {
                gestor.insertarActor(new Actor("id" + i, "Actor " + i + "-" + r.nextInt(1_000_000)));
            }
            ArrayList<Actor> lista = gestor.obtenerActoresOrdenados();
            assertEquals(5000, lista.size());
            assertTrue(estaOrdenada(lista));
        }
    }
}
