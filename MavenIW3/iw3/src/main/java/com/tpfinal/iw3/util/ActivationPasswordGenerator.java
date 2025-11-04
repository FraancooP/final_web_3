package com.tpfinal.iw3.util;

import java.util.concurrent.ThreadLocalRandom;

/**
 * Generador de contraseñas de activación de 5 dígitos.
 * Utilizado en el Punto 2 (Pesaje Inicial) para generar
 * contraseñas únicas que se entregarán al chofer.
 */
public class ActivationPasswordGenerator {

    /**
     * Genera una contraseña de activación de 5 dígitos (10000-99999)
     * @return Número entero de 5 dígitos
     */
    public static Integer generate() {
        // Genera un número aleatorio entre 10000 y 99999 (inclusive)
        return ThreadLocalRandom.current().nextInt(10000, 100000);
    }
}
