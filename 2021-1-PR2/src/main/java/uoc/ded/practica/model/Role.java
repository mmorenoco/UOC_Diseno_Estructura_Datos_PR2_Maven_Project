package uoc.ded.practica.model;

import uoc.ei.tads.*;

public class Role {

    private String roleId;
    private String name;
    private Lista<Worker> workers;

    public Role(String roleId, String name){
        this.roleId = roleId;
        this.name = name;
        this.workers = new ListaEncadenada<Worker>();
    }

    public String getRoleId() {
        return roleId;
    }

    public void setRoleId(String roleId) {
        this.roleId = roleId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void addWorker(Worker worker){
        workers.insertarAlFinal(worker);
    }

    public void removeWorker(Worker worker) {
        workers.borrar(workerToBeDeleted(worker));
    }

    public Posicion<Worker> workerToBeDeleted(Worker worker) {
        Recorrido<Worker> workers = this.workers.posiciones();
        while(workers.haySiguiente()){
            Posicion<Worker> position = workers.siguiente();
            Worker workerToRemove = position.getElem();
            if(workerToRemove.equals(worker)){
                return position;
            }
        }
        return null;
    }

    public Iterador<Worker> workers(){
        return workers.elementos();
    }

    public int numWorkers() {
        return workers.numElems();
    }

    public boolean hasWorkers() {
        return workers.numElems()>0;
    }

    public boolean is(String roleId){
        return this.roleId.equals(roleId);
    }
}
