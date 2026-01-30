'use client';

import { useEffect } from 'react';
import { useRouter } from 'next/navigation';
import Layout from '@/components/Layout';
import { Card, CardHeader, CardContent, CardTitle } from '@/components/Card';
import { useAuthStore } from '@/lib/store';
import { Settings, Shield, Bell, Palette } from 'lucide-react';

export default function SettingsPage() {
  const router = useRouter();
  const { user } = useAuthStore();

  useEffect(() => {
    if (!user) {
      router.push('/login');
      return;
    }

    if (user.role !== 'admin') {
      router.push('/dashboard');
      return;
    }
  }, [user, router]);

  if (!user || user.role !== 'admin') return null;

  return (
    <Layout>
      <div className="mb-8">
        <h1 className="text-3xl font-bold text-gray-900 mb-2">Paramètres système</h1>
        <p className="text-gray-600">Configuration et gestion des paramètres de la plateforme</p>
      </div>

      <div className="grid grid-cols-1 md:grid-cols-2 gap-6">
        <Card>
          <CardHeader>
            <div className="flex items-center space-x-3">
              <Shield className="text-blue-600" size={24} />
              <CardTitle>Sécurité</CardTitle>
            </div>
          </CardHeader>
          <CardContent>
            <p className="text-gray-600 mb-4">
              Gestion des politiques de sécurité, authentification et autorisations.
            </p>
            <button className="text-blue-600 hover:text-blue-700 font-medium">
              Configurer →
            </button>
          </CardContent>
        </Card>

        <Card>
          <CardHeader>
            <div className="flex items-center space-x-3">
              <Bell className="text-green-600" size={24} />
              <CardTitle>Notifications</CardTitle>
            </div>
          </CardHeader>
          <CardContent>
            <p className="text-gray-600 mb-4">
              Paramètres des notifications système et communications.
            </p>
            <button className="text-blue-600 hover:text-blue-700 font-medium">
              Configurer →
            </button>
          </CardContent>
        </Card>

        <Card>
          <CardHeader>
            <div className="flex items-center space-x-3">
              <Palette className="text-purple-600" size={24} />
              <CardTitle>Interface</CardTitle>
            </div>
          </CardHeader>
          <CardContent>
            <p className="text-gray-600 mb-4">
              Personnalisation de l'apparence et des thèmes de la plateforme.
            </p>
            <button className="text-blue-600 hover:text-blue-700 font-medium">
              Configurer →
            </button>
          </CardContent>
        </Card>

        <Card>
          <CardHeader>
            <div className="flex items-center space-x-3">
              <Settings className="text-orange-600" size={24} />
              <CardTitle>Système</CardTitle>
            </div>
          </CardHeader>
          <CardContent>
            <p className="text-gray-600 mb-4">
              Configuration générale du système et maintenance.
            </p>
            <button className="text-blue-600 hover:text-blue-700 font-medium">
              Configurer →
            </button>
          </CardContent>
        </Card>
      </div>

      <Card className="mt-8">
        <CardHeader>
          <CardTitle>Statut du système</CardTitle>
        </CardHeader>
        <CardContent>
          <div className="grid grid-cols-1 md:grid-cols-3 gap-4">
            <div className="text-center">
              <div className="text-2xl font-bold text-green-600">99.9%</div>
              <p className="text-sm text-gray-600">Disponibilité</p>
            </div>
            <div className="text-center">
              <div className="text-2xl font-bold text-blue-600">2.1s</div>
              <p className="text-sm text-gray-600">Temps de réponse moyen</p>
            </div>
            <div className="text-center">
              <div className="text-2xl font-bold text-purple-600">1.2GB</div>
              <p className="text-sm text-gray-600">Utilisation mémoire</p>
            </div>
          </div>
        </CardContent>
      </Card>
    </Layout>
  );
}