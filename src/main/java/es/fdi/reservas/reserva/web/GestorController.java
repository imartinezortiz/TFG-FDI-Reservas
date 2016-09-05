package es.fdi.reservas.reserva.web;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

import es.fdi.reservas.fileupload.business.boundary.NewFileCommand;
import es.fdi.reservas.reserva.business.boundary.GestorService;
import es.fdi.reservas.reserva.business.boundary.GrupoReservaService;
import es.fdi.reservas.reserva.business.boundary.ReservaService;
import es.fdi.reservas.reserva.business.entity.Edificio;
import es.fdi.reservas.reserva.business.entity.Espacio;
import es.fdi.reservas.reserva.business.entity.EstadoReserva;
import es.fdi.reservas.reserva.business.entity.Reserva;
import es.fdi.reservas.users.business.boundary.UserService;
import es.fdi.reservas.users.business.entity.User;

@PreAuthorize("hasRole('ROLE_GESTOR')")
@Controller
public class GestorController {

	private static final Logger logger = LoggerFactory.getLogger(ReservaController.class);
	
	private GestorService gestor_service;
	
	@Autowired
	public GestorController(GestorService gs){
		gestor_service = gs;
	}
	
	@RequestMapping({"/gestor/mis-reservas"})
    public String misReservas() {
        return "redirect:/gestor/mis-reservas/page/1";
    }
	
	@RequestMapping({"/gestor"})
    public String Administrar() {
        return "redirect:/gestor/administrar";
    }
	
	@RequestMapping({"/gestor/administrar/usuarios"})
    public String gestionUsuarios() {
        return "redirect:/gestor/administrar/usuarios/page/1";
    }
	
	@RequestMapping({"/gestor/administrar/edificios"})
    public String gestionEdificios() {
        return "redirect:/gestor/administrar/edificios/page/1";
    }
	
	@RequestMapping({"/gestor/administrar/espacios"})
    public String gestionEspacios() {
        return "redirect:/gestor/administrar/espacios/page/1";
    }
	
	@RequestMapping({"/gestor/administrar/reservas"})
    public String gestionReservas() {
        return "redirect:/gestor/administrar/reservas/page/1";
    }
	
	@RequestMapping({"/gestor/administrar/reservas/user/{userId}"})
    public String gestionReservasFiltroUsuario(@PathVariable String userId) {
        return "redirect:/gestor/administrar/reservas/user/"+userId+"/page/1";
    }
	
	@RequestMapping({"/gestor/administrar/reservas/espacio/{espacioId}"})
    public String gestionReservasFiltroEspacio(@PathVariable String espacioId) {
        return "redirect:/gestor/administrar/reservas/espacio/"+espacioId+"/page/1";
    }
	
	@RequestMapping({"/gestor/administrar/reservas/estado/{estadoReserva}"})
    public String gestionReservasFiltroEstado(@PathVariable String estadoReserva) {
        return "redirect:/gestor/administrar/reservas/estado/"+estadoReserva+"/page/1";
    }
	
	@RequestMapping(value="/gestor/mis-reservas/page/{pageNumber}", method=RequestMethod.GET)
    public String misReservasPaginadas(@PathVariable Integer pageNumber, Model model) {
		User u = gestor_service.getUsuarioActual();
		
		PageRequest pageRequest = new PageRequest(pageNumber - 1, 7, new Sort(new Sort.Order(Sort.Direction.ASC,"comienzo")));
        Page<Reserva> currentResults = gestor_service.getReservasByUserId(u.getId(), pageRequest);
        
        model.addAttribute("currentResults", currentResults);
    
        int current = currentResults.getNumber() + 1;
        int begin = Math.max(1, current - 5);
        int end = Math.min(begin + 10, currentResults.getTotalPages()); 

        model.addAttribute("beginIndex", begin);
        model.addAttribute("endIndex", end);
        model.addAttribute("currentIndex", current); 
		model.addAttribute("view", "mis-reservas");
		model.addAttribute("User", u);
		model.addAttribute("reservasPendientes", gestor_service.getReservasPendientes(u.getId(), EstadoReserva.PENDIENTE));
		model.addAttribute("GruposReservas", gestor_service.getGrupoReservaByUserId(u.getId()));
		model.addAttribute("view", "mis-reservas");
		
        return "index";
    }
	
	@RequestMapping(value="/gestor/administrar",method=RequestMethod.GET)
	public ModelAndView administrar(){
		ModelAndView model = new ModelAndView("index");
		User u = gestor_service.getUsuarioActual();
		model.addObject("User", u);

		model.addObject("reservasPendientes", gestor_service.reservasPendientesUsuario(u.getId(), EstadoReserva.PENDIENTE).size());
		model.addObject("GruposReservas", gestor_service.getGrupoReservaByUserId(u.getId()));
		model.addObject("url","/gestor/administrar" );

		model.addObject("view", "gestor/administrar");

		return model;
	}
	
	@RequestMapping(value="/gestor/administrar/reservas/page/{pageNumber}", method=RequestMethod.GET)
    public String gestiona_reservas(@PathVariable Integer pageNumber, Model model) {
		User u= gestor_service.getUsuarioActual();
		PageRequest pageRequest = new PageRequest(pageNumber - 1, 7, new Sort(new Sort.Order(Sort.Direction.ASC,"comienzo")));
        Page<Reserva> currentResults = gestor_service.getReservasByFacultadId(u.getFacultad().getId(), pageRequest);
        
        model.addAttribute("currentResults", currentResults);
    
        int current = currentResults.getNumber() + 1;
        int begin = Math.max(1, current - 5);
        int end = Math.min(begin + 10, currentResults.getTotalPages()); 

        model.addAttribute("beginIndex", begin);
        model.addAttribute("endIndex", end);
        model.addAttribute("currentIndex", current); 
		model.addAttribute("User", u);
		model.addAttribute("GruposReservas", gestor_service.getGrupoReservaByUserId(u.getId()));
		model.addAttribute("reservasPendientes", gestor_service.getReservasPendientes(u.getId(), EstadoReserva.PENDIENTE).size());
		model.addAttribute("view", "gestor/administrar-reservas");
		
        return "index";
    }
	
	@RequestMapping(value="/gestor/administrar/reservas/user/{user}/page/{pageNumber}", method=RequestMethod.GET)
    public String gestiona_reservas_usuario(@PathVariable String user, @PathVariable Integer pageNumber, Model model) {
		User u = gestor_service.getUsuarioActual();
		
		PageRequest pageRequest = new PageRequest(pageNumber - 1, 5);
        Page<Reserva> currentResults = gestor_service.getReservasByUserId(user, u.getFacultad().getId(), pageRequest);
        
        model.addAttribute("currentResults", currentResults);
    
        int current = currentResults.getNumber() + 1;
        int begin = Math.max(1, current - 5);
        int end = Math.min(begin + 10, currentResults.getTotalPages()); 

        model.addAttribute("beginIndex", begin);
        model.addAttribute("endIndex", end);
        model.addAttribute("currentIndex", current); 
		model.addAttribute("User", u);
		model.addAttribute("GruposReservas", gestor_service.getGrupoReservaByUserId(u.getId()));
		model.addAttribute("reservasPendientes", gestor_service.getReservasPendientes(u.getId(), EstadoReserva.PENDIENTE).size());
		model.addAttribute("view", "gestor/administrar-reservas");
		
        return "index";
    }
	
	@RequestMapping(value="/gestor/administrar/reservas/espacio/{espacio}/page/{pageNumber}", method=RequestMethod.GET)
    public String gestiona_reservas_espacio(@PathVariable String espacio, @PathVariable Integer pageNumber, Model model) {
		User u = gestor_service.getUsuarioActual();
		
		PageRequest pageRequest = new PageRequest(pageNumber - 1, 5);
        Page<Reserva> currentResults = gestor_service.getReservasByEspacioId(espacio, u.getFacultad().getId(), pageRequest);
        
        model.addAttribute("currentResults", currentResults);
    
        int current = currentResults.getNumber() + 1;
        int begin = Math.max(1, current - 5);
        int end = Math.min(begin + 10, currentResults.getTotalPages()); 

        model.addAttribute("beginIndex", begin);
        model.addAttribute("endIndex", end);
        model.addAttribute("currentIndex", current); 
		model.addAttribute("User", u);
		model.addAttribute("GruposReservas", gestor_service.getGrupoReservaByUserId(u.getId()));
		model.addAttribute("reservasPendientes", gestor_service.getReservasPendientes(u.getId(), EstadoReserva.PENDIENTE).size());
		model.addAttribute("view", "gestor/administrar-reservas");
		
        return "index";
    }
	
	@RequestMapping(value="/gestor/administrar/reservas/estado/{estado}/page/{pageNumber}", method=RequestMethod.GET)
    public String gestiona_reservas_estado(@PathVariable String estado, @PathVariable Integer pageNumber, Model model) {
		User u = gestor_service.getUsuarioActual();
		
		PageRequest pageRequest = new PageRequest(pageNumber - 1, 5);
		String estadoAlt;
		char char0= estado.charAt(0);
		if (char0=='c' || char0=='C')
			estadoAlt="Confirmada";
		else if (char0=='p' || char0=='P')
			estadoAlt="Pendiente";
		else if (char0=='d' || char0=='D')
			estadoAlt="Denegada";
		else
			estadoAlt="Otro";
        Page<Reserva> currentResults = gestor_service.getReservasByEstadoReserva(EstadoReserva.fromEstadoReserva(estadoAlt), u.getFacultad().getId(), pageRequest);
        
        model.addAttribute("currentResults", currentResults);
    
        int current = currentResults.getNumber() + 1;
        int begin = Math.max(1, current - 5);
        int end = Math.min(begin + 10, currentResults.getTotalPages()); 

        model.addAttribute("beginIndex", begin);
        model.addAttribute("endIndex", end);
        model.addAttribute("currentIndex", current); 
		model.addAttribute("User", u);
		model.addAttribute("GruposReservas", gestor_service.getGrupoReservaByUserId(u.getId()));
		model.addAttribute("reservasPendientes", gestor_service.getReservasPendientes(u.getId(), EstadoReserva.PENDIENTE).size());
		model.addAttribute("view", "gestor/administrar-reservas");
		
        return "index";
    }
	
	@RequestMapping(value="/gestor/administrar/reservas/editar/{idReserva}", method=RequestMethod.GET)
    public String edita_reservas_gestor(@PathVariable Long idReserva, Model model) {
		
		User u = gestor_service.getUsuarioActual();
		List<EstadoReserva> lista= EstadoReserva.getAll();
		model.addAttribute("User", u);	
		model.addAttribute("Reserva", gestor_service.getReserva(idReserva));
		model.addAttribute("EstadosReserva", lista);
		Long id= gestor_service.getReserva(idReserva).getUser().getId();
		model.addAttribute("GruposReservas", gestor_service.getGrupoReservaByUserId(u.getId()));
		model.addAttribute("GruposReservasUser", gestor_service.getGrupoReservaByUserId(id));
		model.addAttribute("reservasPendientes", gestor_service.getReservasPendientes(u.getId(), EstadoReserva.PENDIENTE).size());
		model.addAttribute("view", "gestor/editarReserva");
		

        return "index";
    }
	
	@RequestMapping(value="/gestor/administrar/edificios/page/{pageNumber}", method=RequestMethod.GET)
    public String gestiona_edificio(@PathVariable Integer pageNumber, Model model) {
		User u= gestor_service.getUsuarioActual();
		PageRequest pageRequest = new PageRequest(pageNumber - 1, 5);
        Page<Edificio> currentResults = gestor_service.getEdificioByFacultadId(u.getFacultad().getId(), pageRequest);
        
        model.addAttribute("currentResults", currentResults);
    
        int current = currentResults.getNumber() + 1;
        int begin = Math.max(1, current - 5);
        int end = Math.min(begin + 10, currentResults.getTotalPages()); 

        model.addAttribute("beginIndex", begin);
        model.addAttribute("endIndex", end);
        model.addAttribute("currentIndex", current); 
		model.addAttribute("User", u);
		model.addAttribute("GruposReservas", gestor_service.getGrupoReservaByUserId(u.getId()));
		model.addAttribute("reservasPendientes", gestor_service.getReservasPendientes(u.getId(), EstadoReserva.PENDIENTE).size());
		model.addAttribute("view", "gestor/administrar_edificios");
		
        return "index";
    }
	
	@RequestMapping(value="/gestor/administrar/edificios/nombre/{nombre}/page/{pageNumber}", method=RequestMethod.GET)
    public String gestiona_edificio_nombre(@PathVariable String nombre, @PathVariable Integer pageNumber, Model model) {
		User u= gestor_service.getUsuarioActual();
		Pageable pageable = new PageRequest(pageNumber - 1, 5);
        Page<Edificio> currentResults = gestor_service.getEdificioByTagNombreAndFacutadId(nombre, u.getFacultad().getId(), pageable);
        
        model.addAttribute("currentResults", currentResults);
    
        int current = currentResults.getNumber() + 1;
        int begin = Math.max(1, current - 5);
        int end = Math.min(begin + 10, currentResults.getTotalPages()); 

        model.addAttribute("beginIndex", begin);
        model.addAttribute("endIndex", end);
        model.addAttribute("currentIndex", current); 
		model.addAttribute("User", u);
		model.addAttribute("GruposReservas", gestor_service.getGrupoReservaByUserId(u.getId()));
		model.addAttribute("reservasPendientes", gestor_service.getReservasPendientes(u.getId(), EstadoReserva.PENDIENTE).size());
		model.addAttribute("view", "gestor/administrar_edificios");
		
        return "index";
    }
	
	@RequestMapping(value="/gestor/administrar/edificios/direccion/{direccion}/page/{pageNumber}", method=RequestMethod.GET)
    public String gestiona_edificio_direccion(@PathVariable String direccion, @PathVariable Integer pageNumber, Model model) {
		User u= gestor_service.getUsuarioActual();
		Pageable pageable = new PageRequest(pageNumber - 1, 5);
        Page<Edificio> currentResults = gestor_service.getEdificioByTagDireccionAndFacutadId(direccion, u.getFacultad().getId(), pageable);
        
        model.addAttribute("currentResults", currentResults);
    
        int current = currentResults.getNumber() + 1;
        int begin = Math.max(1, current - 5);
        int end = Math.min(begin + 10, currentResults.getTotalPages()); 

        model.addAttribute("beginIndex", begin);
        model.addAttribute("endIndex", end);
        model.addAttribute("currentIndex", current); 
		model.addAttribute("User", u);
		model.addAttribute("GruposReservas", gestor_service.getGrupoReservaByUserId(u.getId()));
		model.addAttribute("reservasPendientes", gestor_service.getReservasPendientes(u.getId(), EstadoReserva.PENDIENTE).size());
		model.addAttribute("view", "gestor/administrar_edificios");
		
        return "index";
    }
	
	@RequestMapping(value="/gestor/administrar/edificios/eliminados/page/{pageNumber}", method=RequestMethod.GET)
    public String gestiona_edificio_direccion(@PathVariable Integer pageNumber, Model model) {
		User u= gestor_service.getUsuarioActual();
		Pageable pageable = new PageRequest(pageNumber - 1, 5);
        Page<Edificio> currentResults = gestor_service.getEdificioDeletedByFacultadId(u.getFacultad().getId(), pageable);
        
        model.addAttribute("currentResults", currentResults);
    
        int current = currentResults.getNumber() + 1;
        int begin = Math.max(1, current - 5);
        int end = Math.min(begin + 10, currentResults.getTotalPages()); 

        model.addAttribute("beginIndex", begin);
        model.addAttribute("endIndex", end);
        model.addAttribute("currentIndex", current); 
		model.addAttribute("User", u);
		model.addAttribute("GruposReservas", gestor_service.getGrupoReservaByUserId(u.getId()));
		model.addAttribute("reservasPendientes", gestor_service.getReservasPendientes(u.getId(), EstadoReserva.PENDIENTE).size());
		model.addAttribute("view", "gestor/papelera_edificios");
		
        return "index";
    }
	
	@RequestMapping(value="/gestor/administrar/espacios/page/{pageNumber}", method=RequestMethod.GET)
    public String gestiona_espacio(@PathVariable Integer pageNumber, Model model) {
		User u= gestor_service.getUsuarioActual();
		Pageable pageable = new PageRequest(pageNumber - 1, 5);
        Page<Espacio> currentResults = gestor_service.getEspaciosByFacultad(u.getFacultad().getId(), pageable);
        
        model.addAttribute("currentResults", currentResults);
    
        int current = currentResults.getNumber() + 1;
        int begin = Math.max(1, current - 5);
        int end = Math.min(begin + 10, currentResults.getTotalPages()); 

        model.addAttribute("beginIndex", begin);
        model.addAttribute("endIndex", end);
        model.addAttribute("currentIndex", current); 
		model.addAttribute("User", u);
		model.addAttribute("GruposReservas", gestor_service.getGrupoReservaByUserId(u.getId()));
		model.addAttribute("reservasPendientes", gestor_service.getReservasPendientes(u.getId(), EstadoReserva.PENDIENTE).size());
		model.addAttribute("view", "gestor/administrar_espacios");
		
        return "index";
    }
	
	@RequestMapping(value="/gestor/administrar/edificios/editar/{idEdificio}", method=RequestMethod.GET)
	public String editarEdificio(@PathVariable("idEdificio") long idEdificio, Model model){
		User u = gestor_service.getUsuarioActual();
		model.addAttribute("User", u);
		model.addAttribute("edificio", gestor_service.getEdificio(idEdificio));
		//model.addAttribute("facultades", facultad_service.getFacultades());
		model.addAttribute("command", new NewFileCommand());
		model.addAttribute("idFacultad", u.getFacultad().getId());
		model.addAttribute("reservasPendientes", gestor_service.reservasPendientesUsuario(u.getId(), EstadoReserva.PENDIENTE).size());
		model.addAttribute("view", "gestor/editarEdificio");
		return "index";
	}
	
	@RequestMapping(value="/gestor/administrar/espacios/nombre/{nombre}/page/{pageNumber}", method=RequestMethod.GET)
    public String gestiona_espacio_nombre(@PathVariable String nombre, @PathVariable Integer pageNumber, Model model) {
		User u= gestor_service.getUsuarioActual();
		Pageable pageable = new PageRequest(pageNumber - 1, 5);
        Page<Espacio> currentResults = gestor_service.getEspaciosByFacultadAndNombre(nombre, u.getFacultad().getId(), pageable);
        
        model.addAttribute("currentResults", currentResults);
    
        int current = currentResults.getNumber() + 1;
        int begin = Math.max(1, current - 5);
        int end = Math.min(begin + 10, currentResults.getTotalPages()); 

        model.addAttribute("beginIndex", begin);
        model.addAttribute("endIndex", end);
        model.addAttribute("currentIndex", current); 
		model.addAttribute("User", u);
		model.addAttribute("GruposReservas", gestor_service.getGrupoReservaByUserId(u.getId()));
		model.addAttribute("reservasPendientes", gestor_service.getReservasPendientes(u.getId(), EstadoReserva.PENDIENTE).size());
		model.addAttribute("view", "gestor/administrar_espacios");
		
        return "index";
    }
	
	@RequestMapping(value="/gestor/administrar/espacios/edificio/{edificio}/page/{pageNumber}", method=RequestMethod.GET)
    public String gestiona_espacio_direccion(@PathVariable String edificio, @PathVariable Integer pageNumber, Model model) {
		User u= gestor_service.getUsuarioActual();
		Pageable pageable = new PageRequest(pageNumber - 1, 5);
        Page<Espacio> currentResults = gestor_service.getEspaciosByFacultadAndEdificio(edificio, u.getFacultad().getId(), pageable);
        
        model.addAttribute("currentResults", currentResults);
    
        int current = currentResults.getNumber() + 1;
        int begin = Math.max(1, current - 5);
        int end = Math.min(begin + 10, currentResults.getTotalPages()); 

        model.addAttribute("beginIndex", begin);
        model.addAttribute("endIndex", end);
        model.addAttribute("currentIndex", current); 
		model.addAttribute("User", u);
		model.addAttribute("GruposReservas", gestor_service.getGrupoReservaByUserId(u.getId()));
		model.addAttribute("reservasPendientes", gestor_service.getReservasPendientes(u.getId(), EstadoReserva.PENDIENTE).size());
		model.addAttribute("view", "gestor/administrar_espacios");
		
        return "index";
    }
	
	@RequestMapping(value="/gestor/administrar/espacios/eliminados/page/{pageNumber}", method=RequestMethod.GET)
    public String gestiona_espacio_direccion(@PathVariable Integer pageNumber, Model model) {
		User u= gestor_service.getUsuarioActual();
		Pageable pageable = new PageRequest(pageNumber - 1, 5);
        Page<Espacio> currentResults = gestor_service.getEspaciosDeletedByFacultad(u.getFacultad().getId(), pageable);
        
        model.addAttribute("currentResults", currentResults);
    
        int current = currentResults.getNumber() + 1;
        int begin = Math.max(1, current - 5);
        int end = Math.min(begin + 10, currentResults.getTotalPages()); 

        model.addAttribute("beginIndex", begin);
        model.addAttribute("endIndex", end);
        model.addAttribute("currentIndex", current); 
		model.addAttribute("User", u);
		model.addAttribute("GruposReservas", gestor_service.getGrupoReservaByUserId(u.getId()));
		model.addAttribute("reservasPendientes", gestor_service.getReservasPendientes(u.getId(), EstadoReserva.PENDIENTE).size());
		model.addAttribute("view", "gestor/papelera_espacios");
		
        return "index";
    }
	
	@RequestMapping(value="/gestor/administrar/usuarios/page/{pageNumber}", method=RequestMethod.GET)
    public String gestiona_usuario(@PathVariable Integer pageNumber, Model model) {
		User u= gestor_service.getUsuarioActual();
		Pageable pageable = new PageRequest(pageNumber - 1, 5);
        Page<User> currentResults = gestor_service.getUsuariosByFacultad(u.getFacultad().getId(), pageable);
        
        model.addAttribute("currentResults", currentResults);
    
        int current = currentResults.getNumber() + 1;
        int begin = Math.max(1, current - 5);
        int end = Math.min(begin + 10, currentResults.getTotalPages()); 

        model.addAttribute("beginIndex", begin);
        model.addAttribute("endIndex", end);
        model.addAttribute("currentIndex", current); 
		model.addAttribute("User", u);
		model.addAttribute("GruposReservas", gestor_service.getGrupoReservaByUserId(u.getId()));
		model.addAttribute("reservasPendientes", gestor_service.getReservasPendientes(u.getId(), EstadoReserva.PENDIENTE).size());
		model.addAttribute("view", "gestor/administrar_usuarios");
		
        return "index";
    }
	
	@RequestMapping(value="/gestor/administrar/usuarios/nombre/{nombre}/page/{pageNumber}", method=RequestMethod.GET)
    public String gestiona_usuario_nombre(@PathVariable String nombre, @PathVariable Integer pageNumber, Model model) {
		User u= gestor_service.getUsuarioActual();
		Pageable pageable = new PageRequest(pageNumber - 1, 5);
        Page<User> currentResults = gestor_service.getUsuariosByFacultadAndNombre(nombre, u.getFacultad().getId(), pageable);
        
        model.addAttribute("currentResults", currentResults);
    
        int current = currentResults.getNumber() + 1;
        int begin = Math.max(1, current - 5);
        int end = Math.min(begin + 10, currentResults.getTotalPages()); 

        model.addAttribute("beginIndex", begin);
        model.addAttribute("endIndex", end);
        model.addAttribute("currentIndex", current); 
		model.addAttribute("User", u);
		model.addAttribute("GruposReservas", gestor_service.getGrupoReservaByUserId(u.getId()));
		model.addAttribute("reservasPendientes", gestor_service.getReservasPendientes(u.getId(), EstadoReserva.PENDIENTE).size());
		model.addAttribute("view", "gestor/administrar_usuarios");
		
        return "index";
    }
	
	@RequestMapping(value="/gestor/administrar/usuarios/email/{email}/page/{pageNumber}", method=RequestMethod.GET)
    public String gestiona_usuario_direccion(@PathVariable String email, @PathVariable Integer pageNumber, Model model) {
		User u= gestor_service.getUsuarioActual();
		Pageable pageable = new PageRequest(pageNumber - 1, 5);
        Page<User> currentResults = gestor_service.getUsuariosByFacultadAndEmail(email, u.getFacultad().getId(), pageable);
        
        model.addAttribute("currentResults", currentResults);
    
        int current = currentResults.getNumber() + 1;
        int begin = Math.max(1, current - 5);
        int end = Math.min(begin + 10, currentResults.getTotalPages()); 

        model.addAttribute("beginIndex", begin);
        model.addAttribute("endIndex", end);
        model.addAttribute("currentIndex", current); 
		model.addAttribute("User", u);
		model.addAttribute("GruposReservas", gestor_service.getGrupoReservaByUserId(u.getId()));
		model.addAttribute("reservasPendientes", gestor_service.getReservasPendientes(u.getId(), EstadoReserva.PENDIENTE).size());
		model.addAttribute("view", "gestor/administrar_usuarios");
		
        return "index";
    }
	
	@RequestMapping(value="/gestor/administrar/usuarios/eliminados/page/{pageNumber}", method=RequestMethod.GET)
    public String gestiona_usuario_direccion(@PathVariable Integer pageNumber, Model model) {
		User u= gestor_service.getUsuarioActual();
		Pageable pageable = new PageRequest(pageNumber - 1, 5);
        Page<User> currentResults = gestor_service.getUsuariosDeletedByFacultad(u.getFacultad().getId(), pageable);
        
        model.addAttribute("currentResults", currentResults);
    
        int current = currentResults.getNumber() + 1;
        int begin = Math.max(1, current - 5);
        int end = Math.min(begin + 10, currentResults.getTotalPages()); 

        model.addAttribute("beginIndex", begin);
        model.addAttribute("endIndex", end);
        model.addAttribute("currentIndex", current); 
		model.addAttribute("User", u);
		model.addAttribute("GruposReservas", gestor_service.getGrupoReservaByUserId(u.getId()));
		model.addAttribute("reservasPendientes", gestor_service.getReservasPendientes(u.getId(), EstadoReserva.PENDIENTE).size());
		model.addAttribute("view", "gestor/papelera_usuarios");
		
        return "index";
    }
}
