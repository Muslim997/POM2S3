const { createClient } = require('@supabase/supabase-js');

const supabase = createClient(
  'https://tnjfqtfhcfhlxcaskqey.supabase.co',
  'eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJzdXBhYmFzZSIsInJlZiI6InRncmpmcXRmaGNmbHhjYXNrcWV5Iiwicm9sZSI6ImFub24iLCJpYXQiOjE3NjQ1NTg2NjUsImV4cCI6MjA4MDEzNDY2NX0.Vklp_eHZkLV3b70sswcwKCbzdHp1w15l3BrO88rCfX8'
);

async function checkUsers() {
  try {
    console.log('Vérification des utilisateurs...');

    const { data: users, error } = await supabase
      .from('users')
      .select('email, full_name, role');

    if (error) {
      console.error('Erreur:', error);
      return;
    }

    console.log('Utilisateurs trouvés:', users?.length || 0);
    if (users && users.length > 0) {
      users.forEach(user => {
        console.log(`- ${user.email} (${user.role}): ${user.full_name}`);
      });
    } else {
      console.log('Aucun utilisateur trouvé. Création des comptes de test...');

      const bcrypt = require('bcryptjs');

      // Créer les comptes de test
      const accounts = [
        { email: 'admin@campus.com', password: 'admin123', name: 'Administrateur', role: 'admin' },
        { email: 'teacher@campus.com', password: 'teacher123', name: 'Professeur Dupont', role: 'teacher' },
        { email: 'student@campus.com', password: 'student123', name: 'Étudiant Martin', role: 'student' }
      ];

      for (const account of accounts) {
        const hashedPassword = await bcrypt.hash(account.password, 10);

        const { error: insertError } = await supabase
          .from('users')
          .insert({
            email: account.email,
            password_hash: hashedPassword,
            full_name: account.name,
            role: account.role
          });

        if (insertError) {
          console.error(`Erreur création ${account.email}:`, insertError);
        } else {
          console.log(`✅ ${account.email} créé`);
        }
      }
    }

  } catch (error) {
    console.error('Erreur de connexion:', error.message);
  }
}

checkUsers();