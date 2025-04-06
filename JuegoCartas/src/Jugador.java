import java.util.*;
import javax.swing.JPanel;

public class Jugador {

    private int TOTAL_CARTAS = 10;
    private int MARGEN = 10;
    private int DISTANCIA = 40;

    private Carta[] cartas = new Carta[TOTAL_CARTAS];
    private Random r = new Random();

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

        // --- Detectar pares, ternas, etc ---
        int[] contadores = new int[NombreCarta.values().length];
        ArrayList<Carta>[] cartasPorNombre = new ArrayList[NombreCarta.values().length];
        for (int i = 0; i < cartasPorNombre.length; i++) {
            cartasPorNombre[i] = new ArrayList<>();
        }

        for (Carta c : cartas) {
            int i = c.getNombre().ordinal();
            contadores[i]++;
            cartasPorNombre[i].add(c);
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
                cartasContadas.addAll(cartasPorNombre[i]);
            }
        }

        ArrayList<Carta>[] cartasPorPinta = new ArrayList[4];
        for (int i = 0; i < 4; i++) {
            cartasPorPinta[i] = new ArrayList<>();
        }

        for (Carta c : cartas) {
            int indice = c.getPinta().ordinal(); // ordinal = 0, 1, 2, 3
            cartasPorPinta[indice].add(c);
        }

        for (int i = 0; i < 4; i++) {
            List<Carta> lista = cartasPorPinta[i];
            lista.sort(Comparator.comparingInt(c -> c.getNombre().ordinal()));

            List<Carta> secuencia = new ArrayList<>();
            for (int j = 0; j < lista.size(); j++) {
                if (secuencia.isEmpty() || lista.get(j).getNombre().ordinal() == secuencia.get(secuencia.size() - 1).getNombre().ordinal() + 1) {
                    secuencia.add(lista.get(j));
                } else {
                    if (secuencia.size() >= 3) {
                        if (mensaje.length() == 0) mensaje.append("Se encontraron los siguientes grupos:\n");
                        mensaje.append("Escalera de ")
                               .append(secuencia.size())
                               .append(" cartas de ")
                               .append(Pinta.values()[i])
                               .append(": ")
                               .append(nombresDesdeCartas(secuencia))
                               .append("\n");
                        cartasContadas.addAll(secuencia);
                        hayGrupos = true;
                    }
                    secuencia.clear();
                    secuencia.add(lista.get(j));
                }
            }

            if (secuencia.size() >= 3) {
                if (mensaje.length() == 0) mensaje.append("Se encontraron los siguientes grupos:\n");
                mensaje.append("Escalera de ")
                       .append(secuencia.size())
                       .append(" cartas de ")
                       .append(Pinta.values()[i])
                       .append(": ")
                       .append(nombresDesdeCartas(secuencia))
                       .append("\n");
                cartasContadas.addAll(secuencia);
                hayGrupos = true;
            }
        }

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
