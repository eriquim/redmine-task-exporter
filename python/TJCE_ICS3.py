import pandas as pd
from openpyxl.styles import numbers

# Caminhos dos arquivos
relatorio_path = r'C:\Users\user\OneDrive - DIGISYSTEM\Gestão - TJCE - Python\Serv_Desenvolvimento_Pje2.xlsx'
base_path = r'C:\Users\user\OneDrive - DIGISYSTEM\Gestão - TJCE - Python\Base_Calculo_CS2.xlsx'

# Carregar os dados
df_relatorio = pd.read_excel(relatorio_path)
df_base = pd.read_excel(base_path)

# 🔹 NOVO PASSO: Remover linhas duplicadas com base na coluna 'num_demanda'
if 'num_demanda' in df_relatorio.columns:
    antes = len(df_relatorio)
    df_relatorio = df_relatorio.drop_duplicates(subset=['num_demanda'], keep='first')
    depois = len(df_relatorio)
    print(f"Linhas duplicadas removidas: {antes - depois}")

# 1. Converter colunas de data e formatar como dd/mm/aaaa
for coluna in ['data_criacao_tarefa', 'data_execucao', 'data_transicao', 'data_transicao_homolog']:
    if coluna in df_relatorio.columns:
        df_relatorio[coluna] = pd.to_datetime(df_relatorio[coluna], errors='coerce').dt.strftime('%d/%m/%Y')

# 2. Criar colunas de catálogo para junção
df_relatorio["Catalogo1"] = df_relatorio["atividade"].astype(str) + ":" + df_relatorio["complexidade"].astype(str)
df_base["Catalogo2"] = df_base["Grupo de Atividades"].astype(str) + " - " + df_base["Referencia Calculo"].astype(str)

# 3. Fazer o merge mantendo todas as colunas necessárias
df_merged = pd.merge(
    df_relatorio, 
    df_base, 
    how="left", 
    left_on="Catalogo1", 
    right_on="Catalogo2"
)

# 4. Renomear colunas para corresponder ao relatório de exemplo
df_merged = df_merged.rename(columns={
    "Qauntidade base UST": "Quantidade base UST",
    "Valor": "valor"
})

# 5. Calcular campo final
df_merged["Multiplicacao"] = (
    df_merged["quantidade"] *
    df_merged["Quantidade base UST"] *
    df_merged["valor"]
)

# 6. Selecionar e ordenar as colunas conforme necessidade
colunas_desejadas = [
    'num_demanda', 'complexidade', 'atividade', 'nome_autor_tarefa', 
    'tipo_atividade', 'tipo_demanda', 'situacao', 'dem_relacionada', 
    'id_entrega', 'status_entrega', 'data_execucao', 'status_taf_relacionada', 
    'data_transicao_homolog', 'data_criacao_tarefa', 
    'Catalogo1', 'Catalogo2', 'quantidade', 'Quantidade base UST', 
    'valor', 'Multiplicacao'
]

# Verificar quais colunas existem no DataFrame
colunas_existentes = [col for col in colunas_desejadas if col in df_merged.columns]

# 7. Criar DataFrame final com as colunas desejadas
df_final = df_merged[colunas_existentes]

# 8. Definir o caminho de saída
output_path = r'C:\Users\user\OneDrive - DIGISYSTEM\Gestão - TJCE - Python\Relatorio_Ics.xlsx'

# Exportar para Excel mantendo a formatação
with pd.ExcelWriter(output_path, engine='openpyxl') as writer:
    df_final.to_excel(writer, index=False, sheet_name='Sheet1')
    
    # Acessar a planilha
    workbook = writer.book
    worksheet = writer.sheets['Sheet1']
    
    # Formatar colunas de data
    date_columns = ['data_criacao_tarefa', 'data_execucao', 'data_transicao_homolog']
    for col_idx, col_name in enumerate(df_final.columns):
        if col_name in date_columns:
            for row in range(2, len(df_final) + 2):  
                worksheet.cell(row=row, column=col_idx+1).number_format = 'DD/MM/YYYY'
    
    # Ajustar largura das colunas
    for idx, col in enumerate(df_final.columns):
        max_length = max(
            df_final[col].astype(str).map(len).max(),
            len(str(col))
        )
        worksheet.column_dimensions[chr(65 + idx)].width = max_length + 2

print(f"Relatório gerado com sucesso em: {output_path}")
