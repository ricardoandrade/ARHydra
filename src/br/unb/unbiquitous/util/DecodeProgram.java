package br.unb.unbiquitous.util;

public enum DecodeProgram {
	ZBAR("ZBar"),
	ZXING("ZXing");
	
	private String descicao;
	
	DecodeProgram(String descricao){
		this.descicao = descricao;
	}
	
	public static DecodeProgram getDecodeProgram(String descricao){
		for (DecodeProgram decodeProgram : DecodeProgram.values()) {
			if ( decodeProgram.getDescicao().equals(descricao) ){
				return decodeProgram;
			}
		}
		return null;
	}

	public String getDescicao() {
		return descicao;
	}

	public void setDescicao(String descicao) {
		this.descicao = descicao;
	}
	
}
