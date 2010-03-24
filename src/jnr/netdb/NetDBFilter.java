/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package jnr.netdb;

/**
 *
 */
interface NetDBFilter<T> {
    T filter(NetDBEntry e);
}
