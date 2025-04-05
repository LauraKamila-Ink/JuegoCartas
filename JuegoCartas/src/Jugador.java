import java.util.*;
import javax.swing.JPanel;

public class Jugador {

    private int TOTAL_CARTAS = 10;
    private int MARGEN = 10;
    private int DISTANCIA = 40;

    private Carta[] cartas = new Carta[TOTAL_CARTAS];
    private Random r = new Random(); // la suerte del jugador

    public void repartir() {
        for (int i = 0; i < TOTAL_CARTAS; i++) {
            cartas[i] = new Carta(r);
        }
    }

    public void mostrar(JPanel pnl) {
        pnl.removeAll();
        int posicion = MARGEN + (TOTAL_CARTAS - 1) * DISTANCIA;
        for (Carta carta : cartas) {
            carta.mostrar(pnl, posicion, MARGEN);
            posicion -= DISTANCIA;
        }
        pnl.repaint();
    }

    public String getGrupos() {
        StringBuilder mensaje = new StringBuilder();
        boolean hayGrupos = false;

        // Contador por nombre para detectar pares, ternas, etc.
        int[] contadores = new int[NombreCarta.values().length];
        for (Carta c : cartas) {
            contadores[c.getNombre().ordinal()]++;
        }

        for (int i = 0; i < contadores.length; i++) {
            if (contadores[i] > 1) {
                hayGrupos = true;
                if (mensaje.length() == 0) {
                    mensaje.append("Se encontraron los siguientes grupos:\n");
                }
                mensaje.append(Grupo.values()[contadores[i]])
                       .append(" de ")
                       .append(NombreCarta.values()[i])
                       .append("\n");
            }
        }

        // Agrupar cartas por pinta
        Map<Pinta, List<Integer>> mapaEscaleras = new HashMap<>();
        for (Carta c : cartas) {
            int valor = c.getNombre().ordinal(); // ordinal: posición de la carta
            mapaEscaleras
                .computeIfAbsent(c.getPinta(), k -> new ArrayList<>())
                .add(valor);
        }

        // Verificar escaleras en cada pinta
        for (Map.Entry<Pinta, List<Integer>> entry : mapaEscaleras.entrySet()) {
            Pinta pinta = entry.getKey();
            List<Integer> valores = entry.getValue();
            Collections.sort(valores);

            // Buscar secuencias de 3 o más consecutivas
            List<Integer> secuencia = new ArrayList<>();
            for (int i = 0; i < valores.size(); i++) {
                if (secuencia.isEmpty() || valores.get(i) == secuencia.get(secuencia.size() - 1) + 1) {
                    secuencia.add(valores.get(i));
                } else {
                    if (secuencia.size() >= 3) {
                        if (mensaje.length() == 0) mensaje.append("Se encontraron los siguientes grupos:\n");
                        mensaje.append("Escalera de ")
                               .append(secuencia.size())
                               .append(" cartas de ")
                               .append(pinta)
                               .append(": ")
                               .append(nombresDesdeValores(secuencia))
                               .append("\n");
                        hayGrupos = true;
                    }
                    secuencia.clear();
                    secuencia.add(valores.get(i));
                }
            }

            // Revisar la última secuencia encontrada
            if (secuencia.size() >= 3) {
                if (mensaje.length() == 0) mensaje.append("Se encontraron los siguientes grupos:\n");
                mensaje.append("Escalera de ")
                       .append(secuencia.size())
                       .append(" cartas de ")
                       .append(pinta)
                       .append(": ")
                       .append(nombresDesdeValores(secuencia))
                       .append("\n");
                hayGrupos = true;
            }
        }

        if (!hayGrupos) {
            mensaje.append("No se encontraron figuras");
        }

        return mensaje.toString();
    }

    private String nombresDesdeValores(List<Integer> valores) {
        StringBuilder nombres = new StringBuilder();
        for (int i = 0; i < valores.size(); i++) {
            nombres.append(NombreCarta.values()[valores.get(i)]);
            if (i < valores.size() - 1) {
                nombres.append(", ");
            }
        }
        return nombres.toString();
    }
}
