const { createApp } = Vue;

createApp({
    data() {
        return {
            // Mesmo host da aplicacao: funciona local e dentro do Docker.
            apiBase: '',
            mensagem: { texto: '', tipo: 'ok' },
            secaoAtiva: 'visao-geral',
            alunos: [],
            cursos: [],
            matriculas: [],
            consultaAlunoId: '',
            aluno: { nome: '', email: '' },
            curso: { titulo: '', descricao: '', area: '' },
            matricula: { alunoId: '', cursoId: '', cursoAdicional: false },
            conclusao: { matriculaId: '', mediaFinal: null },
            resgate: { matriculaId: '', cursos: [], escolhidos: [], limite: 3 },
            _timeoutMensagem: null
        };
    },

    computed: {
        totalCursosAdicionais() {
            return this.alunos.reduce((total, a) => total + (a.cursosAdicionaisDisponiveis || 0), 0);
        },
        areasExistentes() {
            return [...new Set(this.cursos.map(c => c.area).filter(Boolean))];
        },
        matriculasEmAndamento() {
            return this.matriculas.filter(m => m.status === 'EM_ANDAMENTO');
        },
        // So tem resgate quem concluiu e ainda tem saldo de cursos adicionais.
        matriculasComDireito() {
            return this.matriculas.filter(m => m.status === 'CONCLUIDO' && m.cursosAdicionaisDisponiveis > 0);
        }
    },

    methods: {
        async request(path, options = {}) {
            const response = await fetch(this.apiBase + path, {
                ...options,
                headers: { 'Content-Type': 'application/json', ...(options.headers || {}) }
            });

            if (response.status === 204) return null;

            const texto = await response.text();
            if (!response.ok) throw new Error(this.extrairErro(texto));
            return texto ? JSON.parse(texto) : null;
        },

        // A API devolve {erro: "..."} ou {erros: {campo: "..."}}.
        extrairErro(texto) {
            try {
                const corpo = JSON.parse(texto);
                if (corpo.erro) return corpo.erro;
                if (corpo.erros) return Object.values(corpo.erros).join(' | ');
            } catch (e) {
                // resposta nao era JSON
            }
            return texto || 'Erro na requisição';
        },

        // A acao pode devolver um texto proprio para sobrescrever a mensagem padrao.
        async executar(acao, sucesso) {
            try {
                const proprio = await acao();
                this.mostrarMensagem(proprio || sucesso, 'ok');
            } catch (erro) {
                this.mostrarMensagem(erro.message, 'erro');
            }
        },

        mostrarMensagem(texto, tipo) {
            clearTimeout(this._timeoutMensagem);
            this.mensagem = { texto, tipo };
            this._timeoutMensagem = setTimeout(() => { this.mensagem = { texto: '', tipo: 'ok' }; }, 5000);
        },

        iniciais(nome) {
            if (!nome) return '?';
            const partes = nome.trim().split(/\s+/);
            const primeira = partes[0][0] || '';
            const ultima = partes.length > 1 ? partes[partes.length - 1][0] : '';
            return (primeira + ultima).toUpperCase();
        },

        async listarAlunos() {
            this.alunos = await this.request('/api/alunos');
        },

        async listarCursos() {
            this.cursos = await this.request('/api/cursos');
        },

        async listarMatriculas() {
            if (!this.consultaAlunoId) {
                this.matriculas = [];
                return;
            }
            this.matriculas = await this.request('/api/matriculas/aluno/' + this.consultaAlunoId);
        },

        async recarregar(alunoId) {
            if (alunoId) this.consultaAlunoId = alunoId;
            await this.listarAlunos();
            await this.listarMatriculas();
        },

        criarAluno() {
            return this.executar(async () => {
                await this.request('/api/alunos', {
                    method: 'POST',
                    body: JSON.stringify(this.aluno)
                });
                this.aluno = { nome: '', email: '' };
                await this.listarAlunos();
            }, 'Aluno cadastrado.');
        },

        criarCurso() {
            return this.executar(async () => {
                await this.request('/api/cursos', {
                    method: 'POST',
                    body: JSON.stringify(this.curso)
                });
                this.curso = { titulo: '', descricao: '', area: '' };
                await this.listarCursos();
            }, 'Curso cadastrado.');
        },

        matricular() {
            return this.executar(async () => {
                const criada = await this.request('/api/matriculas', {
                    method: 'POST',
                    body: JSON.stringify(this.matricula)
                });
                this.matricula = { alunoId: '', cursoId: '', cursoAdicional: false };
                await this.recarregar(criada.alunoId);
            }, 'Matrícula criada.');
        },

        concluir() {
            return this.executar(async () => {
                const concluida = await this.request(
                    '/api/matriculas/' + this.conclusao.matriculaId + '/concluir',
                    { method: 'PUT', body: JSON.stringify({ mediaFinal: this.conclusao.mediaFinal }) }
                );

                const liberados = concluida.cursosAdicionaisLiberados;
                this.conclusao = { matriculaId: '', mediaFinal: null };
                await this.recarregar(concluida.alunoId);

                if (liberados > 0) {
                    return 'Aprovado! Liberou ' + liberados + ' cursos adicionais.';
                }
                return null;
            }, 'Curso concluído. A média não foi acima de 7,0, nenhum curso adicional liberado.');
        },

        abrirResgate() {
            return this.executar(async () => {
                const resposta = await this.request('/api/matriculas/' + this.resgate.matriculaId + '/resgate');
                this.resgate.cursos = resposta.cursosDisponiveis;
                this.resgate.limite = resposta.limiteDeEscolhas;
                this.resgate.escolhidos = [];
            }, 'Escolha os cursos que o aluno quer resgatar.');
        },

        alternarEscolha(cursoId) {
            const escolhidos = this.resgate.escolhidos;
            const posicao = escolhidos.indexOf(cursoId);

            if (posicao >= 0) {
                escolhidos.splice(posicao, 1);
                return;
            }

            if (escolhidos.length >= this.resgate.limite) {
                this.mostrarMensagem('Limite de ' + this.resgate.limite + ' cursos atingido.', 'erro');
                return;
            }

            escolhidos.push(cursoId);
        },

        confirmarResgate() {
            return this.executar(async () => {
                const alunoId = this.consultaAlunoId;

                for (const cursoId of this.resgate.escolhidos) {
                    await this.request('/api/matriculas', {
                        method: 'POST',
                        body: JSON.stringify({ alunoId, cursoId, cursoAdicional: true })
                    });
                }

                this.limparResgate();
                await this.recarregar(alunoId);
            }, 'Cursos resgatados e matrículas criadas.');
        },

        limparResgate() {
            this.resgate = { matriculaId: '', cursos: [], escolhidos: [], limite: 3 };
        },

        classeStatus(status) {
            if (status === 'CONCLUIDO') return 'aprovado';
            if (status === 'CANCELADO') return 'reprovado';
            return 'andamento';
        },

        rotuloStatus(status) {
            if (status === 'CONCLUIDO') return 'concluído';
            if (status === 'CANCELADO') return 'cancelado';
            return 'em andamento';
        },

        // Destaca na barra lateral a secao visivel enquanto o usuario rola a pagina.
        iniciarScrollSpy() {
            const secoes = ['visao-geral', 'alunos', 'cursos', 'matricular', 'concluir', 'resgate', 'consultas']
                .map(id => document.getElementById(id))
                .filter(Boolean);

            if (!secoes.length || !('IntersectionObserver' in window)) return;

            const observer = new IntersectionObserver((entradas) => {
                entradas.forEach(entrada => {
                    if (entrada.isIntersecting) this.secaoAtiva = entrada.target.id;
                });
            }, { rootMargin: '-15% 0px -70% 0px' });

            secoes.forEach(secao => observer.observe(secao));
        }
    },

    mounted() {
        this.listarAlunos();
        this.listarCursos();
        this.iniciarScrollSpy();
    }
}).mount('#app');
