import { NextRequest, NextResponse } from 'next/server';
import bcrypt from 'bcryptjs';
import jwt from 'jsonwebtoken';
import { supabase } from '@/lib/db';

const JWT_SECRET = process.env.JWT_SECRET || 'your-secret-key';

export const dynamic = 'force-dynamic';

export async function POST(request: NextRequest) {
  try {
    const { email, password } = await request.json();

    console.log('Tentative de connexion pour:', email);

    if (!email || !password) {
      return NextResponse.json({ error: 'Email et mot de passe requis' }, { status: 400 });
    }

    // Trouver l'utilisateur dans Supabase
    const { data: user, error } = await supabase
      .from('users')
      .select('*')
      .eq('email', email)
      .single();

    console.log('Utilisateur trouvé:', user ? 'oui' : 'non', 'Erreur:', error);

    if (error || !user) {
      console.log('Utilisateur non trouvé ou erreur');
      return NextResponse.json({ error: 'Email ou mot de passe incorrect' }, { status: 401 });
    }

    // Vérifier le mot de passe
    const isValidPassword = await bcrypt.compare(password, (user as any).password_hash);
    console.log('Mot de passe valide:', isValidPassword);

    if (!isValidPassword) {
      console.log('Mot de passe incorrect');
      return NextResponse.json({ error: 'Email ou mot de passe incorrect' }, { status: 401 });
    }

    console.log('Connexion réussie pour:', email);

    // Créer le token JWT
    const token = jwt.sign(
      { userId: (user as any).id, email: (user as any).email, role: (user as any).role },
      JWT_SECRET,
      { expiresIn: '7d' }
    );

    // Retourner l'utilisateur sans le mot de passe
    const { password_hash, ...userWithoutPassword } = user as any;

    return NextResponse.json({ user: userWithoutPassword, token }, { status: 200 });
  } catch (error) {
    console.error('Erreur lors de la connexion:', error);
    return NextResponse.json({ error: 'Erreur serveur' }, { status: 500 });
  }
}