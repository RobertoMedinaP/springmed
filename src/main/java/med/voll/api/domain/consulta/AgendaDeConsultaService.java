package med.voll.api.domain.consulta;

import med.voll.api.domain.medico.Medico;
import med.voll.api.domain.medico.MedicoRepository;
import med.voll.api.domain.paciente.Paciente;
import med.voll.api.domain.paciente.PacienteRepository;
import med.voll.api.infra.errores.ValidacionDeIntegridad;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AgendaDeConsultaService {

    @Autowired
    private PacienteRepository pacienteRepository;
    @Autowired
    private MedicoRepository medicoRepository;

    @Autowired
    private ConsultaRepository consultaRepository;

    public void agendar(DatosAgendarConsulta datos)  {

        if (pacienteRepository.findById(datos.id()).isPresent()){
            throw new ValidacionDeIntegridad("este id para paciente no fue encontrado");
        }

        if (datos.idMedico()!=null && medicoRepository.existsById(datos.idMedico())){
            throw new ValidacionDeIntegridad("este id para el médico no fue encontrado");
        }

        var paciente= pacienteRepository.findById(datos.idPaciente()).get();

        //var medico=medicoRepository.findById(datos.idMedico()).get();
        var medico= seleccionarMedico(datos);

        var consulta=new Consulta(null,medico, paciente,datos.fecha());
        consultaRepository.save(consulta);
    }

    private Medico seleccionarMedico(DatosAgendarConsulta datos) {
        if (datos.idMedico()!=null){
            return medicoRepository.getReferenceById(datos.idMedico());
        }
        if (datos.especialidad()==null){
            throw new ValidacionDeIntegridad("debe seleccionar una especialidad para el médico");
        }
        return medicoRepository.seleccionarMedicoConEspecialidadEnFecha(datos.especialidad(),datos.fecha());
    }
}
