'use client';

import { useEffect } from 'react';
import { useRouter } from 'next/navigation';
import Link from 'next/link';
import { BookOpen, Users, Award, TrendingUp, ArrowRight } from 'lucide-react';

export default function Home() {
  const router = useRouter();

  useEffect(() => {
    router.push('/login');
  }, [router]);

  return (
    <div className="min-h-screen bg-gradient-to-br from-blue-50 via-white to-blue-50">
      <nav className="border-b border-gray-200 bg-white/80 backdrop-blur-sm fixed w-full z-50">
        <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
          <div className="flex justify-between items-center h-16">
            <div className="text-2xl font-bold text-blue-600">CampusMaster</div>
            <div className="flex space-x-4">
              <Link href="/login">
                <button className="px-4 py-2 text-gray-700 hover:text-blue-600 font-medium transition-colors">
                  Connexion
                </button>
              </Link>
              <Link href="/register">
                <button className="px-6 py-2 bg-blue-600 text-white rounded-lg hover:bg-blue-700 font-medium transition-colors">
                  Inscription
                </button>
              </Link>
            </div>
          </div>
        </div>
      </nav>

      <main className="pt-16">
        <section className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-20">
          <div className="text-center mb-16">
            <h1 className="text-5xl font-bold text-gray-900 mb-6">
              Plateforme pédagogique avancée
              <span className="block text-blue-600 mt-2">pour étudiants de Master 2</span>
            </h1>
            <p className="text-xl text-gray-600 max-w-3xl mx-auto mb-8">
              Gérez vos cours, suivez vos devoirs et communiquez avec vos enseignants sur une plateforme moderne et intuitive.
            </p>
            <div className="flex justify-center space-x-4">
              <Link href="/register">
                <button className="px-8 py-3 bg-blue-600 text-white rounded-lg hover:bg-blue-700 font-medium text-lg transition-colors flex items-center">
                  Commencer
                  <ArrowRight size={20} className="ml-2" />
                </button>
              </Link>
              <Link href="/login">
                <button className="px-8 py-3 bg-white text-gray-900 border-2 border-gray-300 rounded-lg hover:border-blue-600 hover:text-blue-600 font-medium text-lg transition-colors">
                  Se connecter
                </button>
              </Link>
            </div>
          </div>

          <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-4 gap-8 mt-20">
            <div className="bg-white rounded-lg p-6 shadow-sm hover:shadow-md transition-shadow">
              <div className="w-12 h-12 bg-blue-100 rounded-lg flex items-center justify-center mb-4">
                <BookOpen className="text-blue-600" size={24} />
              </div>
              <h3 className="text-xl font-semibold text-gray-900 mb-2">Gestion des cours</h3>
              <p className="text-gray-600">
                Accédez à tous vos cours, supports pédagogiques et ressources en un seul endroit.
              </p>
            </div>

            <div className="bg-white rounded-lg p-6 shadow-sm hover:shadow-md transition-shadow">
              <div className="w-12 h-12 bg-green-100 rounded-lg flex items-center justify-center mb-4">
                <Award className="text-green-600" size={24} />
              </div>
              <h3 className="text-xl font-semibold text-gray-900 mb-2">Suivi des devoirs</h3>
              <p className="text-gray-600">
                Soumettez vos travaux, consultez vos notes et recevez des feedbacks détaillés.
              </p>
            </div>

            <div className="bg-white rounded-lg p-6 shadow-sm hover:shadow-md transition-shadow">
              <div className="w-12 h-12 bg-purple-100 rounded-lg flex items-center justify-center mb-4">
                <Users className="text-purple-600" size={24} />
              </div>
              <h3 className="text-xl font-semibold text-gray-900 mb-2">Communication</h3>
              <p className="text-gray-600">
                Messagerie interne, forums de discussion et annonces en temps réel.
              </p>
            </div>

            <div className="bg-white rounded-lg p-6 shadow-sm hover:shadow-md transition-shadow">
              <div className="w-12 h-12 bg-orange-100 rounded-lg flex items-center justify-center mb-4">
                <TrendingUp className="text-orange-600" size={24} />
              </div>
              <h3 className="text-xl font-semibold text-gray-900 mb-2">Statistiques</h3>
              <p className="text-gray-600">
                Tableaux de bord avancés pour suivre votre progression et performances.
              </p>
            </div>
          </div>
        </section>

        <section className="bg-blue-600 py-16 mt-20">
          <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 text-center">
            <h2 className="text-3xl font-bold text-white mb-4">
              Prêt à commencer ?
            </h2>
            <p className="text-blue-100 text-lg mb-8">
              Rejoignez CampusMaster et transformez votre expérience d'apprentissage.
            </p>
            <Link href="/register">
              <button className="px-8 py-3 bg-white text-blue-600 rounded-lg hover:bg-gray-100 font-medium text-lg transition-colors">
                Créer un compte gratuitement
              </button>
            </Link>
          </div>
        </section>
      </main>

      <footer className="bg-gray-900 text-white py-8 mt-20">
        <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 text-center">
          <p className="text-gray-400">
            © 2025 CampusMaster. Plateforme pédagogique pour Master 2.
          </p>
        </div>
      </footer>
    </div>
  );
}
