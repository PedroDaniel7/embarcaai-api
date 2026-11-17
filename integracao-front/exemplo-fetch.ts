// Exemplo para React/TypeScript - substituir pela URL real do Render.
const API = import.meta.env.VITE_API_URL ?? 'http://localhost:8080';

export type Consulta = {
  id: number;
  pacienteId: number;
  unidadeId: number;
  especialidade: string;
  dataHora: string;
  precisaTransporte: 'S' | 'N';
  status: 'AGENDADA' | 'CONFIRMADA' | 'CANCELADA' | 'REALIZADA' | 'FALTOU';
};

export async function listarConsultas(): Promise<Consulta[]> {
  const response = await fetch(`${API}/consultas`);
  if (!response.ok) throw new Error('Não foi possível carregar as consultas');
  return response.json();
}

export async function cadastrarPaciente(dados: {
  nome: string; telefone: string; dataNascimento: string; zonaRural: 'S'|'N';
}) {
  const response = await fetch(`${API}/pacientes`, {
    method: 'POST',
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify(dados)
  });
  if (!response.ok) throw new Error('Falha ao cadastrar paciente');
  return response.json();
}

export async function consultarOcupacao(viagemId: number) {
  const response = await fetch(`${API}/viagens/${viagemId}/ocupacao`);
  if (!response.ok) throw new Error('Falha ao consultar assentos');
  return response.json();
}
