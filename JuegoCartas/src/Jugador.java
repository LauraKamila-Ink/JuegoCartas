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
        List<Carta> cartasContadas = new ArrayList<>();

        // Contador por nombre para detectar pares, ternas, etc.
        int[] contadores = new int[NombreCarta.values().length];
        Map<NombreCarta, List<Carta>> mapaCartasPorNombre = new HashMap<>();

        for (Carta c : cartas) {
            NombreCarta nombre = c.getNombre();
            contadores[nombre.ordinal()]++;
            mapaCartasPorNombre
                .computeIfAbsent(nombre, k -> new ArrayList<>())
                .add(c);
        }

        for (int i = 0; i < contadores.length; i++) {
            if (contadores[i] > 1) {
                hayGrupos = true;
                if (mensaje.length() == 0) {
                    mensaje.append("Se encontraron los siguientes grupos:\n");
                }
                Grupo grupo = Grupo.values()[contadores[i]];
                mensaje.append(grupo)
                       .append(" de ")
                       .append(NombreCarta.values()[i])
                       .append("\n");
                cartasContadas.addAll(mapaCartasPorNombre.get(NombreCarta.values()[i]));
            }
        }

        // Agrupar cartas por pinta para detectar escaleras
        Map<Pinta, List<Carta>> mapaEscaleras = new HashMap<>();
        for (Carta c : cartas) {
            mapaEscaleras
                .computeIfAbsent(c.getPinta(), k -> new ArrayList<>())
                .add(c);
        }

        for (Map.Entry<Pinta, List<Carta>> entry : mapaEscaleras.entrySet()) {
            Pinta pinta = entry.getKey();
            List<Carta> lista = entry.getValue();

            // Ordenar las cartas por valor
            lista.sort(Comparator.comparingInt(c -> c.getNombre().ordinal()));

            List<Carta> secuencia = new ArrayList<>();
            for (int i = 0; i < lista.size(); i++) {
                if (secuencia.isEmpty() || lista.get(i).getNombre().ordinal() == secuencia.get(secuencia.size() - 1).getNombre().ordinal() + 1) {
                    secuencia.add(lista.get(i));
                } else {
                    if (secuencia.size() >= 3) {
                        if (mensaje.length() == 0) mensaje.append("Se encontraron los siguientes grupos:\n");
                        mensaje.append("Escalera de ")
                               .append(secuencia.size())
                               .append(" cartas de ")
                               .append(pinta)
                               .append(": ")
                               .append(nombresDesdeCartas(secuencia))
                               .append("\n");
                        cartasContadas.addAll(secuencia);
                        hayGrupos = true;
                    }
                    secuencia.clear();
                    secuencia.add(lista.get(i));
                }
            }

            if (secuencia.size() >= 3) {
                if (mensaje.length() == 0) mensaje.append("Se encontraron los siguientes grupos:\n");
                mensaje.append("Escalera de ")
                       .append(secuencia.size())
                       .append(" cartas de ")
                       .append(pinta)
                       .append(": ")
                       .append(nombresDesdeCartas(secuencia))
                       .append("\n");
                cartasContadas.addAll(secuencia);
                hayGrupos = true;
            }
        }

        // Calcular puntos solo con cartas que hacen parte de grupos/escaleras
        int puntos = 0;
        for (Carta c : cartasContadas) {
            puntos += c.getValor();
        }

        if (hayGrupos) {
            mensaje.append("\nTotal de puntos acumulados: ").append(puntos);
        } else {
            mensaje.append("No se encontraron figuras");
        }

        return mensaje.toString();
    }

    private String nombresDesdeCartas(List<Carta> lista) {
        StringBuilder nombres = new StringBuilder();
        for (int i = 0; i < lista.size(); i++) {
            nombres.append(lista.get(i).getNombre());
            if (i < lista.size() - 1) {
                nombres.append(", ");
            }
        }
        return nombres.toString();
    }
}