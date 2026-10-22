package es.ercilla.ejemplos;

import java.time.LocalDate;

import es.ercilla.modelos.Libro;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.EntityTransaction;
import jakarta.persistence.Persistence;

public class EjemploCRUDLibro {
    public static void main(String[] args) {
        EntityManagerFactory emf = null;
        try {
            emf = Persistence.createEntityManagerFactory("ad-clase");

            Libro l1 = new Libro(
                "100 años de soledad",
                LocalDate.of(2000, 5, 1),
                true,
                null
            );

            l1 = insertarNuevoLibro(emf, l1);

            System.out.println("LIBRO ANTES DE ACTUALIZAR");
            System.out.println(l1);
            l1 = actualizarLibroPorId(emf, 5, "Meinkampf", LocalDate.of(1940, 2, 1));
            System.out.println("LIBRO DESPUÉS DE ACTUALIZAR");
            System.out.println(l1);

            l1 = borrarLibroPorId(emf, 1);
        } catch (Exception e) {
            e.printStackTrace();
        }finally{
            if(emf != null && emf.isOpen()){
                emf.close();
            }
        }
    }

    public static Libro insertarNuevoLibro(EntityManagerFactory emf, Libro libro){
        EntityManager em = null;
        EntityTransaction transaction = null;

        try{
            em = emf.createEntityManager();
            transaction = em.getTransaction();
            transaction.begin();
            em.persist(libro);
            transaction.commit();
        } catch (Exception e) {
            if(transaction != null && transaction.isActive()){
                transaction.rollback();
        }
        }
        return libro; // devolvemos el libro persistido en BD
    }

    public static Libro actualizarLibroPorId(EntityManagerFactory emf, Integer id, String nuevoTitulo, LocalDate nuevaFechaPublicacion){
        EntityManager em = emf.createEntityManager();
        EntityTransaction et = em.getTransaction();

        et.begin();

        Libro libro = em.find(Libro.class, id);
        if(libro != null){
            libro.setTitulo(nuevoTitulo);
            libro.setFechaPublicacion(nuevaFechaPublicacion);
            et.commit();
        }else{
            et.rollback();
        }
        return libro;
    }

    public static Libro borrarLibroPorId(EntityManagerFactory emf, Integer id) {
        EntityManager em = emf.createEntityManager();
        EntityTransaction et = em.getTransaction();
        et.begin();
        Libro libro = em.find(Libro.class, id);
        if(libro != null){
            em.remove(libro);
            et.commit();
        }else{
            et.rollback();
        }
        return libro;

    }
}
