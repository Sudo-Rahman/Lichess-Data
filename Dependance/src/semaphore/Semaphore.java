package semaphore;

/**
 * Classe Semaphore.
 *
 * @author Yilmaz Rahman
 * @version 1.0
 * @date 02/04/2022
 */
public class Semaphore
{

    /**
     * @description : Met en attente le thread courant jusqu'à ce que le nombre de threads fini soit égal au nombre de threads fourni en paramètre.
     */
    public synchronized void finished()
    {
        while (count != nbThreads)
        {
            try {wait();} catch (InterruptedException e) {e.printStackTrace();}
        }
    }

    private int count;
    private final int nbThreads;

    /**
     * @param count : nombre de threads.
     */
    public Semaphore(int count)
    {
        this.count = count;
        this.nbThreads = count;
    }

    /**
     * @description : Acquiert un verrou.
     */
    public synchronized void acquire()
    {
        while (count == 0)
        {
            try {wait();} catch (InterruptedException e) {e.printStackTrace();}
        }
        count--;
    }

    /**
     * @description : Libère un verrou.
     */
    public synchronized void release()
    {
        count++;
        notifyAll();
    }
}
