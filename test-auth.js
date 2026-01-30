const bcrypt = require('bcryptjs');

// Simuler la base de données
const users = [
  {
    id: '1',
    email: 'admin@campus.com',
    password_hash: '$2b$10$oeC/z9M/zsqs0TGhosb5ieviBkrzWuifaG7wxlItZFyH5DlZIzxz2', // admin123
    full_name: 'Administrateur',
    role: 'admin'
  },
  {
    id: '2',
    email: 'teacher@campus.com',
    password_hash: '$2b$10$8n/fvX6cKCzZRm31LTzT5.oMq25tl.tuEYccrvJmUYtQ4sz6Gpn3y', // teacher123
    full_name: 'Professeur Dupont',
    role: 'teacher'
  },
  {
    id: '3',
    email: 'student@campus.com',
    password_hash: '$2b$10$EsIa5ChVIBm5IoxpL.U5.e17Yw18CQUcqghZlHJuenFUj5vwfmv4a', // student123
    full_name: 'Étudiant Martin',
    role: 'student'
  }
];

async function testLogin(email, password) {
  console.log(`Test de connexion pour ${email} avec mot de passe ${password}`);

  // Trouver l'utilisateur
  const user = users.find(u => u.email === email);
  if (!user) {
    console.log('❌ Utilisateur non trouvé');
    return;
  }

  console.log('Utilisateur trouvé:', user.email);

  // Vérifier le mot de passe
  const isValidPassword = await bcrypt.compare(password, user.password_hash);
  console.log('Mot de passe valide:', isValidPassword);
  console.log('Hash stocké:', user.password_hash);

  if (isValidPassword) {
    console.log('✅ Connexion réussie!');
  } else {
    console.log('❌ Mot de passe incorrect');
  }
}

async function main() {
  await testLogin('admin@campus.com', 'admin123');
  await testLogin('teacher@campus.com', 'teacher123');
  await testLogin('student@campus.com', 'student123');
  await testLogin('admin@campus.com', 'wrongpassword');
}

main();