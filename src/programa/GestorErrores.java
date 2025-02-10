package programa;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class GestorErrores {

    private List<String> errores = new ArrayList<>();
    private String fichero = null;

    public GestorErrores(String nombreArchivo){
        errores = new ArrayList<>();
        fichero = "src/programa/" + nombreArchivo + ".txt";
    }
    
    // Agregar un error con mensaje
    public void agregarError(String mensaje) {
        errores.add(mensaje);
    }
    
    // Agregar un error con línea y mensaje
    public void agregarError(int linea, String mensaje) {
        errores.add("(linea: " + linea + "): " + mensaje);
    }
    
    // Agregar un error con línea,columna y mensaje
    public void agregarError(int linea,int columna, String mensaje) {
        errores.add("(linea: " + linea +",columna: " + columna + "): " + mensaje);
    }
    
    // Agregar un error con tokens esperados
    public void agregarErrorConTokensEsperados(int linea, int columna, String mensaje, List<String> expectedTokens) {
        if (expectedTokens.isEmpty()) {
            expectedTokens.add("tokens no especificados");
        }        
        String tokens = String.join(", ", expectedTokens);
        errores.add("(linea: " + linea +",columna: " + columna + "): " + mensaje + " Tokens esperados: [" + tokens + "]");
    }
    
    // Verificar si hay errores
    public boolean hayErrores() {
        return !errores.isEmpty();
    }

    // Obtener el número de errores
    public int contarErrores() {
        return errores.size();
    }

    // Devuelve los errores como una lista (opcional, para flexibilidad)
    public List<String> getErrores() {
        return errores;
    }
    
    // elimina todos los errores que hay
    public void reset(){
        errores.clear();
    }
    
    // Imprimir todos los errores en un fichero, añadiendo cabecera y manteniendo errores previos
    public void imprimirErrores(String cabecera) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(fichero, true))) { // 'true' para añadir contenido
            writer.write("\n" + cabecera + "\n");
            writer.write("=".repeat(cabecera.length()) + "\n"); // Línea divisoria
            for (String error : errores) {
                writer.write(error + "\n");
            }
            writer.write("\n"); // Separador final
        } catch (IOException e) {
            System.err.println("Error al escribir los errores en el archivo: " + e.getMessage());
        }
    }
}