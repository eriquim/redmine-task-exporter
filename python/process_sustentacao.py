import pandas as pd
from datetime import timedelta, datetime
import re

# --- Configurações ---
file_path = r'C:\Users\user\OneDrive - DIGISYSTEM\Gestão - TJCE - Python\Sustentação\Rel. Sustentação Pje.xls'
output_path = r'C:\Users\user\OneDrive - DIGISYSTEM\Gestão - TJCE - Python\Sustentação\Relatorio_Indicadores_Sustentacao.xlsx'

# --- Regras de Negócio ---
priority_rules = {
    '5 - Muito Alto': timedelta(hours=6),
    '4 - Alto': timedelta(hours=11),
    '3 - Médio': timedelta(hours=55),
    '2 - Baixo': timedelta(hours=77),
    '1 - Muito Baixo': timedelta(hours=110)
}

status_nao_contam = [
    'SUSPJE - Ag. Homologação', 'SUSPJE - Ag. Hmg. Release', 'SUSPJE - Ag. Execução Script',
    'SUSPJE - Ag. Corr. Release', 'SUSPJE - Ag. Info.(Triagem)', 'SUSPJE - Ag. Info.(Impl)',
    'SUSPJE - Ag. Correção'
]

status_inicio_contagem = 'SUSPJE - Backlog'
status_resolvida = 'SUSPJE - Resolvida'
status_cancelada = 'SUSPJE - Cancelada'

# --- Funções ---
def calculate_business_hours(start_time, end_time, ticket_history):
    total_seconds = 0
    current_time = start_time

    # Otimização: Pré-calcular os intervalos de pausa
    paused_intervals = []
    for i in range(len(ticket_history) - 1):
        row = ticket_history.iloc[i]
        next_row = ticket_history.iloc[i+1]
        if row['status_new'] in status_nao_contam:
            paused_intervals.append((row['updated_on'], next_row['updated_on']))

    # Itera dia a dia, e dentro de cada dia, hora a hora
    while current_time.date() <= end_time.date():
        if current_time.weekday() < 5:  # Dias úteis
            day_start_hour = 8
            day_end_hour = 19

            # Define o início e fim do dia de trabalho
            business_day_start = current_time.replace(hour=day_start_hour, minute=0, second=0, microsecond=0)
            business_day_end = current_time.replace(hour=day_end_hour, minute=0, second=0, microsecond=0)

            # Define o período de contagem para o dia atual
            count_start = max(current_time, business_day_start)
            count_end = min(end_time, business_day_end)

            if count_start < count_end:
                # Calcula a duração total do período de contagem no dia
                total_day_seconds = (count_end - count_start).total_seconds()

                # Subtrai os períodos de pausa
                for pause_start, pause_end in paused_intervals:
                    overlap_start = max(count_start, pause_start)
                    overlap_end = min(count_end, pause_end)
                    if overlap_start < overlap_end:
                        total_day_seconds -= (overlap_end - overlap_start).total_seconds()
                
                total_seconds += max(0, total_day_seconds)

        # Avança para o próximo dia
        current_time = (current_time + timedelta(days=1)).replace(hour=0, minute=0, second=0, microsecond=0)

    return total_seconds / 3600  # Retorna em horas

# --- Script Principal ---
try:
    df = pd.read_excel(file_path)
    
    # Renomeando colunas para facilitar o uso ANTES de qualquer outra operação com elas
    df = df.rename(columns={'criacao_tarefa': 'created_on', 'atualizacao_tarefa': 'updated_on'})

    # Convertendo colunas de data para datetime com formato explícito
    df['created_on'] = pd.to_datetime(df['created_on'], format='%d/%m/%Y %H:%M:%S', errors='coerce')
    df['updated_on'] = pd.to_datetime(df['updated_on'], format='%d/%m/%Y %H:%M:%S', errors='coerce')

    # Remover linhas com datas inválidas após a conversão
    df.dropna(subset=['created_on', 'updated_on'], inplace=True)

    # Normaliza espaços nos status: substitui múltiplos espaços por um único espaço e remove espaços extras
    df['status_new'] = df['status_new'].astype(str).str.replace(r'\s+', ' ', regex=True).str.strip()
    df['prioridade'] = df['prioridade'].astype(str)
    df['atribuido_para'] = df['atribuido_para'].astype(str) # Garante que 'atribuido_para' é string

    df_filtered = df[df['status_new'] != status_cancelada].copy()

    results = []
    unique_ticket_ids = df_filtered['id'].unique()

    print(f"Processando todos os chamados resolvidos...")
    for ticket_id in unique_ticket_ids:
        ticket_history = df_filtered[df_filtered['id'] == ticket_id].sort_values(by='updated_on')
        last_status_row = ticket_history.iloc[-1]

        if last_status_row['status_new'] == status_resolvida:

            end_time = last_status_row['updated_on']
            prioridade = last_status_row['prioridade']
            autor = last_status_row['atribuido_para'] # Pega o autor

            start_time_counting = None
            for _, h_row in ticket_history.iterrows():
                if h_row['status_new'] == status_inicio_contagem:
                    start_time_counting = h_row['updated_on']
                    break
            
            if start_time_counting:
                time_spent = calculate_business_hours(start_time_counting, end_time, ticket_history)
                
                # Corrigindo a extração da chave de prioridade de forma mais robusta
                match = re.match(r'(\d+\s*-\s*[a-zA-ZáéíóúÁÉÍÓÚãõÃÕçÇ]+\s*[a-zA-ZáéíóúÁÉÍÓÚãõÃÕçÇ]*)', prioridade)
                priority_key = match.group(1).strip() if match else prioridade.strip()

                expected_time = priority_rules.get(priority_key, timedelta(0)).total_seconds() / 3600

                results.append({
                    'ID do Chamado': ticket_id,
                    'Prioridade': prioridade,
                    'Autor': autor, # Adiciona o autor aqui
                    'Data de Resolução': end_time,
                    'Tempo Gasto (horas)': time_spent,
                    'Tempo Gasto (minutos)': time_spent * 60,
                    'Tempo Esperado (horas)': expected_time,
                    'Tempo Esperado (minutos)': expected_time * 60,
                    'Resolvido no Prazo': 'Sim' if time_spent <= expected_time else 'Não'
                })

    results_df = pd.DataFrame(results)
    results_df.to_excel(output_path, index=False)

    print("--- Relatório de Indicadores de Sustentação ---")
    print(f"Total de chamados resolvidos: {len(results_df)}")
    if not results_df.empty:
        print(results_df)
    print(f"\nRelatório salvo em: {output_path}")

except Exception as e:
    print(f"Ocorreu um erro: {e}")
