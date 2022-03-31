/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package hr.algebra.dal;

/**
 *
 * @author Tomo
 */
public class RepositoryFactory {

    public static Repository getRepository() {
        return new SqlRepository();
    }

}
