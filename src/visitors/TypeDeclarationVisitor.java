package visitors;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jdt.core.dom.ASTVisitor;
//import org.eclipse.jdt.core.dom.ITypeBinding;
import org.eclipse.jdt.core.dom.TypeDeclaration;

public class TypeDeclarationVisitor extends ASTVisitor {
	List<TypeDeclaration> listTypeDeclaration = new ArrayList<TypeDeclaration>();
	
	public boolean visit(TypeDeclaration typeDeclaration) {
		//ITypeBinding typeBind = typeDeclaration.resolveBinding();
        //ITypeBinding superTypeBind = typeBind.getSuperclass();
        //ITypeBinding[] interfaceBinds = typeBind.getInterfaces();      
		listTypeDeclaration.add(typeDeclaration);
        return true;
	}
	
	public List<TypeDeclaration> getListTypeDeclaration() {
		return listTypeDeclaration;
	}
}
