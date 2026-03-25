public class Usuario {
    private String nome;
    private String endereco;
    private String cidade;
    private String email;

    public Usuario(String nome, String endereco, String cidade, String email) {
        this.nome = nome;
        this.endereco = endereco;
        this.cidade = cidade;
        this.email = email;
    }

    public String getNome() {
        return nome;
    }

    public String getEndereco() {
        return endereco;
    }

    public String getCidade() {
        return cidade;
    }

    public String getEmail() {
        return email;
    }

    @Override
    public String toString() {
        return String.format("%s (%s, %s, %s)", nome, email, cidade, endereco);
    }
}
