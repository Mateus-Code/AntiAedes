package com.example.antiaedes.converter;

import com.example.antiaedes.entities.Denuncia;
import com.example.antiaedes.entities.Endereco;
import com.example.antiaedes.entities.Funcionario;
import com.example.antiaedes.entities.Usuario;
import com.example.antiaedes.entities.Visita;

import org.ksoap2.serialization.SoapObject;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Vector;

public class Converter {

    public static Usuario getUser(SoapObject response) {
        if (response != null) {
            Usuario usuario = new Usuario();
            usuario.setId(Integer.parseInt(response.getPropertyAsString("id")));
            usuario.setNome(response.getPropertyAsString("nome"));
            usuario.setEmail(response.getPropertyAsString("email"));
            usuario.setSaldo(Integer.parseInt(response.getPropertyAsString("saldo")));
            usuario.setAtivo(Boolean.parseBoolean(response.getPropertyAsString("ativo")));
            return usuario;
        } else return null;
    }

    public static SoapObject userToSoap(String NAMESPACE, Usuario usuario) {
        SoapObject user = new SoapObject(NAMESPACE, "usuario");
        user.addProperty("nome", usuario.getNome());
        user.addProperty("email", usuario.getEmail());
        user.addProperty("senha", usuario.getSenha());
        return user;
    }

    public static Funcionario getFunctionary(SoapObject response) {
        if (response != null) {
            Funcionario funcionario = new Funcionario();
            funcionario.setId(Integer.parseInt(response.getPropertyAsString("id")));
            funcionario.setCpf(response.getPropertyAsString("cpf"));
            funcionario.setNome(response.getPropertyAsString("nome"));
            funcionario.setEmail(response.getPropertyAsString("email"));
            funcionario.setAtivo(Boolean.parseBoolean(response.getPropertyAsString("ativo")));
            return funcionario;
        } else return null;
    }

    public static SoapObject denunciationToSoap(String NAMESPACE, Denuncia denuncia) {
        SoapObject functionary = new SoapObject(NAMESPACE, "denuncia");
        functionary.addProperty("cep", denuncia.getCep());
        functionary.addProperty("bairro", denuncia.getBairro());
        functionary.addProperty("rua", denuncia.getRua());
        functionary.addProperty("referencia", denuncia.getReferencia());
        functionary.addProperty("descricao", denuncia.getDescricao());
        functionary.addProperty("num_casa", denuncia.getNum_casa());
        functionary.addProperty("latitude", denuncia.getLatitude());
        functionary.addProperty("longitude", denuncia.getLongitude());
        functionary.addProperty("data", denuncia.getData());
        functionary.addProperty("tipo", denuncia.getTipo());
        functionary.addProperty("imagem", denuncia.getImagem());
        functionary.addProperty("codigo", denuncia.getCodigo());
        functionary.addProperty("propriedade", denuncia.isPrioridade());
        functionary.addProperty("id_user", denuncia.getId_user());
        functionary.addProperty("id_fun", denuncia.getId_fun());
        return functionary;
    }

    public static SoapObject visitToSoap(String NAMESPACE, Visita visita) {
        SoapObject visit = new SoapObject(NAMESPACE, "visita");
        visit.addProperty("id_fun", visita.getId_fun());
        visit.addProperty("id_den", visita.getId_den());
        visit.addProperty("data", visita.getData());
        visit.addProperty("situacao", visita.getSituation());
        visit.addProperty("observacao", visita.getObservacao());
        return visit;
    }

    public static Denuncia getDenunciation(SoapObject response) {
        if (response != null) {
            Denuncia denuncia = new Denuncia();
            denuncia.setId(Integer.parseInt(response.getPropertyAsString("id")));
            denuncia.setCep(response.getPropertyAsString("cep"));
            denuncia.setBairro(response.getPropertyAsString("bairro"));
            denuncia.setRua(response.getPropertyAsString("rua"));
            denuncia.setReferencia(response.getPropertyAsString("referencia"));
            denuncia.setDescricao(response.getPropertyAsString("descricao"));
            denuncia.setNum_casa(Integer.parseInt(response.getPropertyAsString("num_casa")));
            denuncia.setLatitude(response.getPropertyAsString("latitude"));
            denuncia.setLongitude(response.getPropertyAsString("longitude"));
            denuncia.setData(response.getPropertyAsString("data"));
            denuncia.setTipo(Integer.parseInt(response.getPropertyAsString("tipo")));
            denuncia.setImagem(response.getPrimitivePropertyAsString("imagem"));
            denuncia.setCodigo(Integer.parseInt(response.getPrimitivePropertyAsString("codigo")));
            denuncia.setPrioridade(Boolean.parseBoolean(response.getPropertyAsString("prioridade")));
            denuncia.setId_fun(Integer.parseInt(response.getPropertyAsString("id_fun")));
            denuncia.setId_user(Integer.parseInt(response.getPropertyAsString("id_user")));
            return denuncia;
        } else return null;
    }

    public static Endereco getAddress(SoapObject response) {
        if (response != null) {
            Endereco endereco = new Endereco();
            endereco.setBairro(response.getPropertyAsString("bairro"));
            endereco.setCep(response.getPropertyAsString("cep"));
            endereco.setCidade(response.getPropertyAsString("cidade"));
            endereco.setComplemento1(response.getPropertyAsString("complemento"));
            endereco.setComplemento2(response.getPropertyAsString("complemento2"));
            endereco.setEnd(response.getPropertyAsString("end"));
            endereco.setUf(response.getPropertyAsString("uf"));
            return endereco;
        } else return null;
    }

    public static ArrayList<Visita> getVisits(Vector<SoapObject> response) {
        ArrayList<Visita> visits = new ArrayList<>();
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        for (SoapObject object : response) {
            Visita visit = new Visita();
            visit.setId_fun(Integer.parseInt(object.getPropertyAsString("id_fun")));
            visit.setId_den(Integer.parseInt(object.getPropertyAsString("id_den")));
            visit.setSituation(Integer.parseInt(object.getPropertyAsString("situacao")));
            visit.setData(object.getPropertyAsString("date"));
            visits.add(visit);
        }
        return visits;
    }
    public static ArrayList<Denuncia> getDenunciations(Vector<SoapObject> response) {
        ArrayList<Denuncia> denuncias = new ArrayList<>();
        for (SoapObject object : response) {
            Denuncia denuncia = new Denuncia();
            denuncia.setId(Integer.parseInt(object.getPropertyAsString("id")));
            denuncia.setCep(object.getPropertyAsString("cep"));
            denuncia.setBairro(object.getPropertyAsString("bairro"));
            denuncia.setRua(object.getPropertyAsString("rua"));
            denuncia.setReferencia(object.getPropertyAsString("referencia"));
            denuncia.setDescricao(object.getPropertyAsString("descricao"));
            denuncia.setNum_casa(Integer.parseInt(object.getPropertyAsString("num_casa")));
            denuncia.setLatitude(object.getPropertyAsString("latitude"));
            denuncia.setLongitude(object.getPropertyAsString("longitude"));
            denuncia.setData(object.getPropertyAsString("data"));
            denuncia.setTipo(Integer.parseInt(object.getPropertyAsString("tipo")));
            denuncia.setImagem(object.getPrimitivePropertyAsString("imagem"));
            denuncia.setCodigo(Integer.parseInt(object.getPrimitivePropertyAsString("codigo")));
            denuncia.setPrioridade(Boolean.parseBoolean(object.getPropertyAsString("prioridade")));
            denuncia.setId_fun(Integer.parseInt(object.getPropertyAsString("id_fun")));
            denuncia.setId_user(Integer.parseInt(object.getPropertyAsString("id_user")));
            denuncias.add(denuncia);
        }
        return denuncias;
    }
}
