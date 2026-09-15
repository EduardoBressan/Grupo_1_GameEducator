const { createApp } = Vue;

createApp({
    data() {
        return {
            // Mesmo host da aplicacao: funciona local e dentro do Docker.
            apiBase: '',
            mensagem: '',
            alunos: [],
            cursos: [],
            matriculas: [],
            consultaAlunoId: '',
            aluno: { nome: '', email: '' },
            curso: { titulo: '', descricao: '' },
            matricula: { alunoId: '', cursoId: '', cursoAdicional: false },
            conclusao: { matriculaId: '', mediaFinal: null }
        };
    },
    methods: {
        async request(path, options = {}) {
            const response = await fetch(this.apiBase + path, {
                ...options,
                headers: { 'Content-Type': 'application/json', ...(options.headers || {}) }
            });
            if (response.status === 204) return null;
            const texto = await response.text();
            if (!response.ok) throw new Error(texto || 'Erro na requisicao');
            return texto ? JSON.parse(texto) : null;
        },
        async executar(acao, sucesso) {
            try {
                await acao();
                this.mensagem = sucesso;
            } catch (erro) {
                this.mensagem = 'Erro: ' + erro.message;
            }
        },
        async listarAlunos() {
            this.alunos = await this.request('/api/alunos');
        },
        async listarCursos() {
            this.cursos = await this.request('/api/cursos');
        },
        async listarMatriculas() {
            if (!this.consultaAlunoId) return;
            this.matriculas = await this.request('/api/matriculas/aluno/' + this.consultaAlunoId);
        },
        criarAluno() {
            return this.executar(async () => {
                await this.request('/api/alunos', { method: 'POST', body: JSON.stringify(this.aluno) });
                this.aluno = { nome: '', email: '' };
                await this.listarAlunos();
            }, 'Aluno criado.');
        },
        criarCurso() {
            return this.executar(async () => {
                await this.request('/api/cursos', { method: 'POST', body: JSON.stringify(this.curso) });
                this.curso = { titulo: '', descricao: '' };
                await this.listarCursos();
            }, 'Curso criado.');
        },
        matricular() {
            return this.executar(async () => {
                const criada = await this.request('/api/matriculas', {
                    method: 'POST',
                    body: JSON.stringify(this.matricula)
                });
                this.consultaAlunoId = criada.alunoId;
                await this.listarMatriculas();
                await this.listarAlunos();
            }, 'Matricula criada.');
        },
        concluir() {
            return this.executar(async () => {
                const concluida = await this.request(
                    '/api/matriculas/' + this.conclusao.matriculaId + '/concluir',
                    { method: 'PUT', body: JSON.stringify({ mediaFinal: this.conclusao.mediaFinal }) }
                );
                this.consultaAlunoId = concluida.alunoId;
                await this.listarMatriculas();
                await this.listarAlunos();
            }, 'Curso concluido. Veja o saldo de cursos adicionais do aluno na lista acima.');
        }
    },
    mounted() {
        this.listarAlunos();
        this.listarCursos();
    }
}).mount('#app');
